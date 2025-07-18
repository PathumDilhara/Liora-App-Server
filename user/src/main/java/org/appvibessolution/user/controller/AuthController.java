package org.appvibessolution.user.controller;

import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/user/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Register new user
    @PostMapping("/create")
    public String createUser(@RequestBody CreateUserDTO userDTO) {
        return authenticationService.createUser(userDTO);
    }

    // Login user, return JWT token
    @PostMapping("/login")
    public String login(@RequestBody LoginUserDTO userDTO) {
        return authenticationService.loginUser(userDTO);
    }

    // Refresh JWT token (optional)
}
