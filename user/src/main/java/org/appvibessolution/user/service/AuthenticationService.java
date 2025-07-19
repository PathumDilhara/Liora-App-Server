package org.appvibessolution.user.service;

import jakarta.mail.MessagingException;
import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.GetUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.dto.VerifyUserEmailDTO;
import org.appvibessolution.user.enums.AccountStatus;
import org.appvibessolution.user.exception.*;
import org.appvibessolution.user.model.AppUser;
import org.appvibessolution.user.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthenticationService {

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final EmailService emailService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public AuthenticationService(
            JWTService jwtService,
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper,
            UserRepo userRepo, EmailService emailService
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    public GetUserDTO createUser(CreateUserDTO userDTO) {

        try {

            if (userRepo.existsByEmail(userDTO.getEmail())) {
                throw new DuplicateResourceException("Email already in use");
            }

            AppUser user = modelMapper.map(userDTO, AppUser.class);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            prepareAndSendVerificationEmail(user);

            return new GetUserDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getCity(),
                    user.getCountry(),
                    user.getProfilePictureUrl(),
                    user.getDateOfBirth()
            );
        } catch (Exception exception){
            throw new UserRegistrationException("User registration failed");
        }
    }

    private void safeSaveUser(AppUser user) {
        try {
            userRepo.save(user);
        } catch (Exception e) {
            throw new UserRegistrationException("User save failed");
        }
    }

    // Verify user email address // verify button
    public String verifyUserEmail(VerifyUserEmailDTO verifyEmailDTO){
        AppUser user = userRepo.findByEmail(verifyEmailDTO.getEmail()).orElseThrow(()->
                new UserNotFoundException("User not found"));

        if(user.getVerificationExpirationAt().isBefore(LocalDateTime.now())){
            throw new VerificationCodeExpirationException("Verification code has expired");
        }

        if(user.getVerificationCode().equals(verifyEmailDTO.getVerificationCode())){
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setVerificationCode(null);
            user.setVerificationExpirationAt(null);

            safeSaveUser(user);

            return jwtService.generateToken(new UserPrincipal(user));
        } else {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }
    }

    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000)+100000;
        return String.valueOf(code);
    }

    public void sendVerificationEmail(AppUser user) throws MessagingException {
        String subject = "Account verification";
        String verificationCode =  user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

            emailService.senderVerificationEmail(user.getEmail(), subject, htmlMessage);
    }

    public void resendVerificationEmail(String email) throws MessagingException {
        AppUser user = userRepo.findByEmail(email).orElseThrow(
                ()->new UserNotFoundException("User not found"));

        if(user.getAccountStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }

        prepareAndSendVerificationEmail(user);
    }

    private void prepareAndSendVerificationEmail(AppUser user) {
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpirationAt(LocalDateTime.now().plusMinutes(10));

        try {
            sendVerificationEmail(user);
        } catch (MessagingException e) {
            throw new VerificationEmailSendFailedException("Failed to send verification email");
        }

        safeSaveUser(user);
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

                safeSaveUser(user);

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
            throw new UserAccountNotEnabledException(
                    "Account not enabled. Please verify your email or contact support.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            userDTO.getEmail(),
                            userDTO.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                user.setFailedLoginAttempts(0);

                safeSaveUser(user);

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

            safeSaveUser(user);

            throw new InvalidCredentialException("Invalid credentials", attempts);
        }

        throw new UserLoginException("Login fails, please try again");
    }

    public String deleteUserById(String id){
        AppUser user = userRepo.findById(id).orElseThrow(()-> new UserNotFoundException("USer not found"));

        try {
            userRepo.deleteById(id);
        } catch (Exception e) {
            throw new UserRegistrationException("User registration failed");
        }

        return "User : " +user.getFirstName() + " " + user.getLastName() + " with id : " + user.getUserId();
    }
}


//        userRepo.findById(loginUserDTO.getEmail());
//        if(loginUserDTO.getPassword().equals(searchUsers(loginUserDTO.getEmail()).get(0).getPassword())){
//            return "loginUserDTO";
//        }