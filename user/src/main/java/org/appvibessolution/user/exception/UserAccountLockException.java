package org.appvibessolution.user.exception;

public class UserAccountLockException extends RuntimeException{
    public UserAccountLockException(String message){
        super(message);
    }
}
