package com.anisaalawiyah.assignmentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDto {
    private String email;
    private String firs_name;
    private String last_name;
    private String password;

   
}
