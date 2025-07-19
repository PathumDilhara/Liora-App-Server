package org.appvibessolution.user.dto;

import lombok.Data;

@Data
public class VerifyUserEmailDTO {
    private String email;
    private String verificationCode;
}
