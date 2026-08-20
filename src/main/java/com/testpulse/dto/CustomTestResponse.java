package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTestResponse {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String durationMinutes;
    private String mode;
    private String difficulty;
    private String testType;
    private boolean active;
    private List<Long> questionIds;
}
