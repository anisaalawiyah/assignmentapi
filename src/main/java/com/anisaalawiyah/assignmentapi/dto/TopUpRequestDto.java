package com.anisaalawiyah.assignmentapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(hidden = true)
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class TopUpRequestDto {
    private int top_up_amount;

   
}
