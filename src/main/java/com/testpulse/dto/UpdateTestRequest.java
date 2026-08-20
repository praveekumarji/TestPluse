package com.testpulse.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTestRequest {
    @Size(min = 1)
    private String title;

    @Size(min = 1)
    private String titleHi;

    @Size(min = 1)
    private String subject;

    @Size(min = 1)
    private String subjectHi;

    @Size(min = 1)
    private String description;

    @Size(min = 1)
    private String descriptionHi;

    @Size(min = 1)
    private String durationMinutes;

    @Size(min = 1)
    private String mode;

    @Size(min = 1)
    private String difficulty;

    @Size(min = 1)
    private String testType;

    private Boolean active;
}
