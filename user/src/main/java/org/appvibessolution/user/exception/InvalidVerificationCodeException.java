package org.appvibessolution.user.exception;

public class InvalidVerificationCodeException extends RuntimeException{
    public InvalidVerificationCodeException(String message){
        super(message);
    }
}
