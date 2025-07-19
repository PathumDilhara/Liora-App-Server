package org.appvibessolution.user.exception;

public class VerificationEmailSendFailedException extends RuntimeException {
    public VerificationEmailSendFailedException(String message) {
        super(message);
    }
}
