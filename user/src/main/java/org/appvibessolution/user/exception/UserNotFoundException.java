package org.appvibessolution.user.exception;

public class UserNotFoundException extends RuntimeException {
    public  UserNotFoundException(String message){
        super(message);
    }
}
