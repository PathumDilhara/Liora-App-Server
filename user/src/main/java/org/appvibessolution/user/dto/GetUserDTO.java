package org.appvibessolution.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private String profilePictureUrl;
    private LocalDate dateOfBirth;
}
