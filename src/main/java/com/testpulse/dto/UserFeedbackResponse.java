package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserFeedbackResponse {
    private boolean success;
    private String feedbackId;
    private String message;
}