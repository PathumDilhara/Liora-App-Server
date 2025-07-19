package org.appvibessolution.user.exception;

import lombok.Getter;

@Getter
public class InvalidCredentialException extends RuntimeException{

    private final int attempts;

    public InvalidCredentialException(String message, int attempts){
        super(message);
        this.attempts =attempts;
    }
}
