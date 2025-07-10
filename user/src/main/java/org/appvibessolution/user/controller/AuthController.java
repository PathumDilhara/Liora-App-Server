package org.appvibessolution.user.controller;

import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/user/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // ------------  Authentication (Login & Register) ------------

    // Register new user
    @PostMapping("/create")
    public CreateUserDTO createUser(@RequestBody CreateUserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    // Login user, return JWT token
    @GetMapping("/login")
    public LoginUserDTO login(@RequestBody LoginUserDTO userDTO) {
        return userService.loginUser(userDTO);
    }

    // Logout user, Invalidate token (optional)
    @PostMapping("/logout")
    public String logout() {
        return userService.logoutUser();
    }

    // Refresh JWT token (optional)
}
