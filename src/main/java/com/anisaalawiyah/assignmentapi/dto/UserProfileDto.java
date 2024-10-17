package com.anisaalawiyah.assignmentapi.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Blob;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private Blob profilePhotoUrl;
}
