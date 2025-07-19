package org.appvibessolution.user.controller;

import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.GetUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.dto.VerifyUserEmailDTO;
import org.appvibessolution.user.exception.CustomResponse;
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
    public CustomResponse<GetUserDTO> createUser(@RequestBody CreateUserDTO userDTO) {
        GetUserDTO getUserDTO = authenticationService.createUser(userDTO);
        return new CustomResponse<>(true, "Verification code sent", getUserDTO);
    }

    // Login user, return JWT token
    @PostMapping("/login")
    public CustomResponse<String> login(@RequestBody LoginUserDTO userDTO) {
        String token = authenticationService.loginUser(userDTO);
        return new CustomResponse<>(true, "User logging success", token);
    }

    // TODO: improve
    @DeleteMapping("/delete/{id}")
    public CustomResponse<String> deleteUserById(@PathVariable String id){
        String response = authenticationService.deleteUserById(id);
        return new CustomResponse<>(true,"User delete success", response);
    }

    @PostMapping("/verifyEmail")
    public CustomResponse<Object> verifyUserEmail(@RequestBody VerifyUserEmailDTO userEmailDTO){
        try {
            String token = authenticationService.verifyUserEmail(userEmailDTO);
            return new CustomResponse<>(true, "User authenticated successfully", token);
        } catch (Exception exception){
            return new CustomResponse<>(false, "Failed", null);
        }
    }

    @PostMapping("/resendVerifyEmail/{email}")
    public CustomResponse<Object> resendVerifyUserEmail(@PathVariable String email){
        try {
            authenticationService.resendVerificationEmail(email);
            return new CustomResponse<>(true, "Verification resend success", null);
        } catch (Exception exception){
            return new CustomResponse<>(false, "Verification Resending Failed", exception.getMessage() );
        }
    }

    // Refresh JWT token (optional)
}
