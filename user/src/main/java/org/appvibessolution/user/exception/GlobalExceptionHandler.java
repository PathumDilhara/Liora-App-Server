package org.appvibessolution.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle any unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Object>> handleAllExceptions(Exception ex){

        // Return a generic user-friendly response
        String message = "Something went wrong. Please try again later";
        CustomResponse<Object> response = new CustomResponse<>(false, message, null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    // When a EmailNotFoundException is thrown anywhere in the app, this method will handle it.
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomResponse<Object>> handleUserNotFoundException(UserNotFoundException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
    }

    // When user try login 5 attempts account will be locked, this error will throw
    @ExceptionHandler(UserAccountLockException.class)
    public ResponseEntity<CustomResponse<Object>> handleUserAccountLockedException(UserAccountLockException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.LOCKED); // 423
    }

    // Handling invalid password entering
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<CustomResponse<Object>> handleInvalidCredentialException(InvalidCredentialException ex){
        Map<String, Integer> data = new HashMap<>();
        data.put("Attempts", ex.getAttempts());
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CustomResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // Handle same type error INTERNAL_SERVER_ERROR
    @ExceptionHandler({
            UserRegistrationException.class,
            VerificationEmailSendFailedException.class,
            UserLoginException.class
    })
    public ResponseEntity<CustomResponse<Object>> handleServerSideExceptions(RuntimeException  ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    @ExceptionHandler(VerificationCodeExpirationException.class)
    public ResponseEntity<CustomResponse<Object>> handleVerificationCodeExpirationException(VerificationCodeExpirationException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.GONE); // 410
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<CustomResponse<Object>> handleInvalidVerificationCodeException(InvalidVerificationCodeException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY); // 422
    }

    @ExceptionHandler(UserAccountNotEnabledException.class)
    public ResponseEntity<CustomResponse<Object>> handleUserAccountNotEnabledException(UserAccountNotEnabledException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN ); // 423
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<CustomResponse<Object>> handleAccountAlreadyVerifiedException(AccountAlreadyVerifiedException ex){
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT ); // 409
    }

}



//    @ExceptionHandler(UserRegistrationException.class)
//    public ResponseEntity<CustomResponse<Object>> handleUserRegistrationException(UserRegistrationException ex){
//        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
//    }

//    @ExceptionHandler(VerificationEmailSendFailedException.class)
//    public ResponseEntity<CustomResponse<Object>> handleVerificationEmailSendFailedException(VerificationEmailSendFailedException ex){
//        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
//    }