package org.appvibessolution.user.exception;

public class AccountAlreadyVerifiedException extends RuntimeException{
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
