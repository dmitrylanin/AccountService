package account.controller;

import account.model.UpdateUserRole;
import account.model.UserBlockDTO;
import account.response.UserData;
import account.service.UserEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserManagementController {
    private final UserEngine userEngine;

    public UserManagementController(UserEngine userEngine){
        this.userEngine = userEngine;
    }

    @PutMapping("/api/admin/user/role")
    public ResponseEntity changeUserRole(@RequestBody UpdateUserRole updateUserRole){
        return userEngine.updateRole(updateUserRole);
    }

    @DeleteMapping("/api/admin/user/{email}")
    public ResponseEntity deleteUser(@PathVariable String email){
        return userEngine.deleteUserByEmail(email);
    }

    @GetMapping("/api/admin/user/")
    public List<UserData> displayUsers(){
        return userEngine.getAllUsers();
    }

    @PutMapping("/api/admin/user/access")
    public ResponseEntity unlockUser(@RequestBody UserBlockDTO userBlockDTO){
        return userEngine.updateUserBlock(userBlockDTO.getEmail(), userBlockDTO.getOperation());
    }
}