package com.anisaalawiyah.assignmentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameRequestDto {
    private String firstName;
    private String lastName;
}
