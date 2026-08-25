package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTestRequest {
    @NotBlank
    private String title;

    private String titleHi;

    @NotBlank
    private String subject;

    private String subjectHi;

    @NotBlank
    private String description;

    private String descriptionHi;

    private String durationMinutes;

    @NotBlank
    private String mode;

    @NotBlank
    private String difficulty;

    @Builder.Default
    private String testType = "FREE";
}
