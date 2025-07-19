package org.appvibessolution.user.service;

import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.exception.InvalidCredentialException;
import org.appvibessolution.user.exception.UserAccountLockException;
import org.appvibessolution.user.exception.UserNotFoundException;
import org.appvibessolution.user.model.AppUser;
import org.appvibessolution.user.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public AuthenticationService(
            JWTService jwtService,
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper,
            UserRepo userRepo
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
    }

    public String createUser(CreateUserDTO userDTO) {
        AppUser user = modelMapper.map(userDTO, AppUser.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));

        return token;
    }

    public String loginUser(LoginUserDTO userDTO) {

        // System.out.println("########## : "+ userDTO.getEmail() + userDTO.getPassword());

        AppUser user = userRepo.findByEmail(userDTO.getEmail()).orElseThrow(
                ()->new UserNotFoundException("User not found"));

        if(user.isAccountLocked()){
            Duration lockDuration = Duration.between(user.getLockTime(), LocalDateTime.now());

            if(lockDuration.toMinutes() >= 10){
                // Auto unlock & save in database
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepo.save(user);
            } else {
                throw new UserAccountLockException("Account locked. Try again after 10 minutes.");
            }
        }

        // let verify/authenticate user
       return authenticateUser(userDTO, user);
    }

    public String authenticateUser(LoginUserDTO userDTO, AppUser user){

        UserPrincipal userPrincipal = new UserPrincipal(user);

        if(!userPrincipal.isEnabled()){
            throw new RuntimeException("Account not enabled. Please try again");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            userDTO.getEmail(),
                            userDTO.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                user.setFailedLoginAttempts(0);
                userRepo.save(user);
                return jwtService.generateToken(new UserPrincipal(user));
            }
        } catch (BadCredentialsException exception){
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            // System.out.println("############### attempts : " +attempts);

            if (attempts >= 5) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
            }

            userRepo.save(user);
            throw new InvalidCredentialException("Invalid credentials", attempts);
        }

        return  "Login fails, please try again";
    }

    public String deleteUserById(String id){
        AppUser user = userRepo.findById(id).orElseThrow(()-> new UserNotFoundException("USer not found"));
        userRepo.deleteById(id);
        return "User : " +user.getFirstName() + " " + user.getLastName() + " with id : " + user.getUserId();
    }
}


//        userRepo.findById(loginUserDTO.getEmail());
//        if(loginUserDTO.getPassword().equals(searchUsers(loginUserDTO.getEmail()).get(0).getPassword())){
//            return "loginUserDTO";
//        }