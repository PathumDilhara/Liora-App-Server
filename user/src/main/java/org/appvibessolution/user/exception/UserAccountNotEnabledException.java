package org.appvibessolution.user.exception;

public class UserAccountNotEnabledException extends RuntimeException{
    public UserAccountNotEnabledException(String message){
        super(message);
    }
}
