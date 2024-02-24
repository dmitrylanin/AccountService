package account.service;

import account.entity.AppUser;
import account.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;

    public AppUserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = repository
                .findAppUserByEmail(email.toLowerCase()).orElseThrow(() -> new UsernameNotFoundException("Not found"));

        user.getAllRoles();

        return new AppUserAdapter(user);
    }
}
