package com.testpulse.dto;

import com.testpulse.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String mobileNumber;
    private String fullName;
    private String avatarUrl;
    private boolean isEmailVerified;
    private String authProvider;
    private String role;
    private String targetExam;
    private java.time.LocalDateTime createdAt;
    private String preferredLanguage;
    private SubscriptionStatus subscriptionStatus;
    private String subscriptionPlan;
    private java.time.LocalDateTime subscriptionExpiry;
    private boolean hasUsedTrial;
    private String message;
}
