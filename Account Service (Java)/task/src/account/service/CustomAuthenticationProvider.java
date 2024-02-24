package account.service;

import account.entity.AppUser;
import account.repository.AppUserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static account.entity.Action.*;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditActionsWriter auditActionsWriter;

    public CustomAuthenticationProvider(AppUserRepository appUserRepository,
                                        PasswordEncoder passwordEncoder,
                                        EntityManagerFactory entityManagerFactory,
                                        AuditActionsWriter auditActionsWriter) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditActionsWriter = auditActionsWriter;
    }

    @Override
    public Authentication authenticate(Authentication authentication){
        String password = authentication.getCredentials().toString();
        Optional<AppUser> optionalAppUserser = appUserRepository.findAppUserByEmail(authentication.getName());

        if(optionalAppUserser.isEmpty()){
            auditActionsWriter.auditActionNotice(LOGIN_FAILED,
                    authentication.getName(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI());
        }

        AppUser user = optionalAppUserser.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            auditActionsWriter.auditActionNotice(LOGIN_FAILED,
                    user.getEmail(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI());
            userBlockEngine(user);
            throw new BadCredentialsException("Very bad password - error in CustomAuthenticationProvider");
        }

        AppUserAdapter appUserAdapter = new AppUserAdapter(user);

        return new UsernamePasswordAuthenticationToken(
                appUserAdapter, password, appUserAdapter.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private void userBlockEngine(AppUser user){
        boolean isUserBlocked = false;

        if(user.getBruteForceCount()+1 > 5){
            user.setBruteForceCount(user.getBruteForceCount()+1);
            if(!user.isBlocked()){
                user.setBlocked(true);
                isUserBlocked = true;
            }
            auditActionsWriter.auditActionNotice(BRUTE_FORCE,
                    user.getEmail(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI());
        }else{
            user.setBruteForceCount(user.getBruteForceCount()+1);
        }

        appUserRepository.save(user);

        if(isUserBlocked){
            auditActionsWriter.auditActionNotice(LOCK_USER,
                    user.getEmail(),
                    "Lock user " + user.getEmail().toLowerCase(),
                    ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI());
        }
    }
}