package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponse {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String durationMinutes;
    private String mode;
    private String difficulty;
}
