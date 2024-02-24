package account.service;

import account.entity.AppUser;
import account.response.CustomErrorMessage;
import account.response.CustomSuccessMessage;
import account.model.UpdateUserRole;
import account.response.UserData;
import account.repository.AppUserRepository;
import account.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static account.entity.Action.*;

@Service
public class UserEngine {
    private final AppUserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AuditActionsWriter auditActionsWriter;

    UserEngine(AppUserRepository userRepository, GroupRepository groupRepository, AuditActionsWriter auditActionsWriter){
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.auditActionsWriter = auditActionsWriter;
    }
    public List<UserData> getAllUsers(){
        List<AppUser> appUsers = userRepository.findAll();

        List<UserData> userDataList = appUsers.stream()
                .map(x-> new UserData(x.getId(),
                            x.getName(),
                            x.getLastname(),
                            x.getEmail(),
                            x.getOrderedRoleNames())
                        )
                .collect(Collectors.toList());

        if(userDataList.size()>0){
            return userDataList;
        }else{
            return null;
        }
    }
    public ResponseEntity deleteUserByEmail(String email){
        Optional <AppUser> optionalUser = userRepository.findAppUserByEmail(email);

        if((!email.matches(".+@acme.com"))
                || (!optionalUser.isPresent())){
            return new ResponseEntity<>(new CustomErrorMessage(404,
                    "User not found!",
                    "/api/admin/user/" + email,
                    "Not Found"), HttpStatus.NOT_FOUND);
        }else{
            AppUser user = optionalUser.get();

            Set<String> roles = user.getRoles().stream()
                    .map(x-> x.getRoleName())
                    .collect(Collectors.toSet());

            if (roles.contains("ROLE_ADMINISTRATOR")){
                return new ResponseEntity<>(new CustomErrorMessage(400,
                        "Can't remove ADMINISTRATOR role!",
                        "/api/admin/user/" + email,
                        "Bad Request"), HttpStatus.BAD_REQUEST);
            }else{
                userRepository.delete(user);

                AppUserAdapter details = (AppUserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                auditActionsWriter.auditActionNotice(DELETE_USER,
                        details.getUsername(),
                        email,
                        "/api/admin/user");

                CustomSuccessMessage successMessage = new CustomSuccessMessage();
                successMessage.setStatus("Deleted successfully!");
                successMessage.setUser(email);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(successMessage);
            }
        }
    }
    public ResponseEntity updateRole(UpdateUserRole newUserRole){
        List<String> strRoles = groupRepository.findAll().stream()
                .map(x-> x.getRoleName())
                .collect(Collectors.toList());

        Optional<AppUser> oUser = userRepository.findAppUserByEmail(newUserRole.getEmail().toLowerCase());

        if(oUser.isEmpty()){
            return new ResponseEntity<>(new CustomErrorMessage(404,
                    "User not found!",
                    "/api/admin/user/role",
                    "Not Found"), HttpStatus.NOT_FOUND);
        }

        AppUser user = oUser.get();

        List<String> userStrRoles = user.getRoles().stream()
                .map(x-> x.getRoleName())
                .collect(Collectors.toList());

        if(!strRoles.contains("ROLE_"+newUserRole.getRole())){
            return new ResponseEntity<>(new CustomErrorMessage(404,
                    "Role not found!",
                    "/api/admin/user/role",
                    "Not Found"), HttpStatus.NOT_FOUND);
        }

        if(user.getOrderedRoles().get(0).getRoleName().equals("ROLE_ADMINISTRATOR") &&
                newUserRole.getOperation().equals("REMOVE") &&
                (newUserRole.getRole().equals("ADMINISTRATOR"))
        ){
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "Can't remove ADMINISTRATOR role!",
                    "/api/admin/user/role",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }

        if((!userStrRoles.contains("ROLE_"+newUserRole.getRole()) &&
                newUserRole.getOperation().equals("REMOVE"))
        ){
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "The user does not have a role!",
                    "/api/admin/user/role",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }

        if((user.getOrderedRoles().size() == 1) &&
                newUserRole.getOperation().equals("REMOVE")
        ){
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "The user must have at least one role!",
                    "/api/admin/user/role",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }

        if (newUserRole.getRole().equals("ADMINISTRATOR")){
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "The user cannot combine administrative and business roles!",
                    "/api/admin/user/role",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }


        if(user.getOrderedRoles().get(0).getRoleName().equals("ROLE_ADMINISTRATOR") &&
                newUserRole.getOperation().equals("GRANT") &&
                (newUserRole.getRole().equals("ACCOUNTANT") || newUserRole.getRole().equals("USER") || newUserRole.getRole().equals("AUDITOR"))
        ){
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "The user cannot combine administrative and business roles!",
                    "/api/admin/user/role",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }

        if(newUserRole.getOperation().equals("GRANT")){
            user.getRoles().add(groupRepository.findAllByRoleName("ROLE_"+newUserRole.getRole()).get(0));
            userRepository.save(user);

            auditActionsWriter.auditActionNotice(
                    GRANT_ROLE,
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal().getName(),
                    "Grant role " + newUserRole.getRole() + " to " + newUserRole.getEmail().toLowerCase(),
                    "/api/admin/user/role");

        }else if(newUserRole.getOperation().equals("REMOVE")){
            user.getRoles().remove(groupRepository.findAllByRoleName("ROLE_"+newUserRole.getRole()).get(0));
            user.getAllRoles();
            userRepository.save(user);

            auditActionsWriter.auditActionNotice(REMOVE_ROLE,
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal().getName(),
                    "Remove role " + newUserRole.getRole() + " from " + newUserRole.getEmail().toLowerCase(),
                    "/api/admin/user/role");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserData(user.getId(),
                        user.getName(),
                        user.getLastname(),
                        user.getEmail().toLowerCase(),
                        user.getOrderedRoleNames())
                );
    }

    public ResponseEntity updateUserBlock(String email, String operation){
        AppUser user = userRepository.findAppUserByEmail(email.toLowerCase()).get();

        Set<String> roles = user.getRoles().stream()
                .map(x-> x.getRoleName())
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_ADMINISTRATOR")) {
            return new ResponseEntity<>(new CustomErrorMessage(400,
                    "Can't lock the ADMINISTRATOR!",
                    "/api/admin/user/access",
                    "Bad Request"), HttpStatus.BAD_REQUEST);
        }

        boolean isUnblocked = false;

        if(operation.equals("LOCK")){
            user.setBlocked(true);
        }else{
            user.setBlocked(false);
            user.setBruteForceCount(0);
            isUnblocked = true;
        }
        userRepository.save(user);

        auditActionsWriter.auditActionNotice(UNLOCK_USER,
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal().getName(),
                "Unlock user " + email.toLowerCase(),
                "/api/admin/user/access");

        CustomSuccessMessage successMessage = new CustomSuccessMessage();
        successMessage.setStatus("User " + email.toLowerCase() + " " + operation.toLowerCase() + "ed!");
        return ResponseEntity.status(HttpStatus.OK)
                .body(successMessage);
    }
}
