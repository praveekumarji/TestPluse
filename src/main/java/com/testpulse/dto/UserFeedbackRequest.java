package com.testpulse.dto;

import com.testpulse.model.FeedbackCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserFeedbackRequest {
    @NotBlank
    private String userId;

    @NotBlank
    @Email
    private String userEmail;

    @NotNull
    private FeedbackCategory category;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String message;

    private String appVersion;

    @Valid
    private DeviceInfo deviceInfo;

    @Valid
    private FeedbackContext context;

    private OffsetDateTime createdAt;

    @Data
    public static class DeviceInfo {
        private String osVersion;
        private String deviceModel;
    }

    @Data
    public static class FeedbackContext {
        private String testId;
        private String questionId;
        private String subject;
    }
}