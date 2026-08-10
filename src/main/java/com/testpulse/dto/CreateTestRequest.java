package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTestRequest {
    private String title;
    private String titleHi;
    private String subject;
    private String subjectHi;
    private String description;
    private String descriptionHi;
    private String durationMinutes;
    private String mode;
    private String difficulty;
}
