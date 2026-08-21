package com.testpulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_feedback")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedback {
    @Id
    @Column(length = 40)
    private String feedbackId;

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 255)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeedbackCategory category;

    private Integer rating;

    @Column(nullable = false, length = 5000)
    private String message;

    private String appVersion;

    @Embedded
    private DeviceInfo deviceInfo;

    @Embedded
    private FeedbackContext context;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceInfo {
        private String osVersion;
        private String deviceModel;
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeedbackContext {
        private String testId;
        private String questionId;
        private String subject;
    }
}