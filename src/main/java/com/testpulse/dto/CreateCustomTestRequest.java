package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomTestRequest {
    @NotBlank
    private String subject;

    @NotBlank
    private String difficulty;

    @NotBlank
    private String mode;

    @Size(max = 50)
    private List<Long> questionIds;
}
