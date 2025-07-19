package org.appvibessolution.user.exception;

public class VerificationCodeExpirationException extends RuntimeException {
    public VerificationCodeExpirationException(String message){
        super(message);
    }
}
