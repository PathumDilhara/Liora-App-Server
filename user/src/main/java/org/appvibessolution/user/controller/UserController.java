package org.appvibessolution.user.controller;

import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.GetUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ------------  User Profile Management ------------

    // For analytics only for admin
    @GetMapping("/allUsers")
    public List<GetUserDTO> getAllUser(){
        return userService.getAllUser();
    }

    // Get public user (not any relation)
    @GetMapping("/id/{id}")
    public GetUserDTO getPublicUserById(@PathVariable(value = "id") String userId){
        return userService.getPublicUserById(userId);
    }

    // Current user
    @GetMapping("/me")
    public CreateUserDTO getCurrentUser() {
        String userId = "fewf";
        return userService.getCurrentUser(userId);
    }

    // Update current user
    @PutMapping("/update")
    public String updateUser(@RequestBody CreateUserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    // Deactivate or soft-delete user account
    @DeleteMapping("/delete")
    public Boolean deleteCurrentUser(){
        return true;
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable String id){
        return userService.deleteUser(id);
    }

    // ------------  Authentication (Login & Register) ------------

    // Register new user
    @PostMapping("/auth/create")
    public CreateUserDTO createUser(@RequestBody CreateUserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    // Login user, return JWT token
    @GetMapping("/auth/login")
    public LoginUserDTO login(@RequestBody LoginUserDTO userDTO) {
        return userService.loginUser(userDTO);
    }

    // Invalidate token (optional)
    @PostMapping("/auth/logout")
    public String logout() {
        return userService.logoutUser();
    }

    // Refresh JWT token (optional)

    // ------------ User Search / Discovery ------------

    // Search user by
    @GetMapping("/search/{searchTerm}")
    public List<CreateUserDTO> searchUsers(@PathVariable String searchTerm) {
        return userService.searchUsers(searchTerm);
    }

    // Get random or suggested profiles
    @GetMapping("/randomList")
    public List<CreateUserDTO> getRandomUser() {
        return userService.getRandomUser();
    }
}
