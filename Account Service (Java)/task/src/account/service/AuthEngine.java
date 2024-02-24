package account.service;

import account.entity.AppUser;
import account.model.*;
import account.repository.AppUserRepository;
import account.repository.GroupRepository;
import account.response.CustomErrorMessage;
import account.response.CustomSuccessMessage;
import account.response.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import static account.entity.Action.CHANGE_PASSWORD;
import static account.entity.Action.CREATE_USER;

@Service
public class AuthEngine {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final PasswordCheck passwordCheck;
    private final AuditActionsWriter auditActionsWriter;

    public AuthEngine(AppUserRepository repository,
                          PasswordEncoder passwordEncoder,
                          PasswordCheck passwordCheck,
                          GroupRepository groupRepository,
                          AuditActionsWriter auditActionsWriter){
        this.appUserRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordCheck = passwordCheck;
        this.groupRepository = groupRepository;
        this.auditActionsWriter = auditActionsWriter;
    }

    public ResponseEntity newUserRegistration(Auth auth){
        var user = new AppUser();
        user.setName(auth.getName());
        user.setLastname(auth.getLastname());
        user.setEmail(auth.getEmail().toLowerCase());

        if(passwordCheck.checkPassword(auth.getPassword())){
            user.setPassword(passwordEncoder.encode(auth.getPassword()));
        }

        if((appUserRepository.findAppUserByEmail(auth.getEmail().toLowerCase()).isPresent())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomErrorMessage(
                            HttpStatus.BAD_REQUEST.value(),
                            "User exist!",
                            "/api/auth/signup",
                            "Bad Request"));
        }

        appUserRepository.save(user);

        if(appUserRepository.count() == 1){
            user.setRoles(new HashSet<>(groupRepository.findAllByRoleName("ROLE_ADMINISTRATOR")));
        }else{
            user.setRoles((new HashSet<>(groupRepository.findAllByRoleName("ROLE_USER"))));
        }

        appUserRepository.save(user);
        auditActionsWriter.auditActionNotice(CREATE_USER,
                null,
                auth.getEmail().toLowerCase(),
                "/api/auth/signup");

        AppUser newUser = appUserRepository.findAppUserByEmail(auth.getEmail().toLowerCase()).get();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserData(newUser.getId(), newUser.getName(), newUser.getLastname(), newUser.getEmail().toLowerCase(), newUser.getOrderedRoleNames()));

    }
    public ResponseEntity<Object> changePass(ChangedPassword changedPassword){
        String newPass = changedPassword.getPassword();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = appUserRepository.findAppUserByEmail(auth.getName().toLowerCase()).get();

        if(passwordEncoder.matches(newPass, currentUser.getPassword())){
            throw new BusinessException("The passwords must be different!");
        }

        if(passwordCheck.checkPassword(newPass)){
            currentUser.setPassword(passwordEncoder.encode(newPass));
            appUserRepository.save(currentUser);
        }

        CustomSuccessMessage successMessage = new CustomSuccessMessage();
        successMessage.setStatus("The password has been updated successfully");
        successMessage.setEmail(currentUser.getEmail());

        auditActionsWriter.auditActionNotice(CHANGE_PASSWORD,
                currentUser.getEmail(),
                currentUser.getEmail().toLowerCase(),
                "/api/auth/changepass");

        return ResponseEntity.status(HttpStatus.OK)
                .body(successMessage);
    }
}