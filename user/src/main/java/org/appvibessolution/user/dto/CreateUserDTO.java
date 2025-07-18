package org.appvibessolution.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.appvibessolution.user.enums.AccountStatus;
import org.appvibessolution.user.enums.Gender;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String profilePictureUrl;
    private String city;
    private String country;
    private AccountStatus state;
    private String bio;
}
