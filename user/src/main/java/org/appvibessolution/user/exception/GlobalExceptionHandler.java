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

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<CustomResponse<Object>> handleInvalidCredentialException(InvalidCredentialException ex){
        Map<String, Integer> data = new HashMap<>();
        data.put("Attempts", ex.getAttempts());
        CustomResponse<Object> response = new CustomResponse<>(false, ex.getMessage(), data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }
}
