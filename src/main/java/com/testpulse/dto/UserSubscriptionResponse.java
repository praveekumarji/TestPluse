package com.testpulse.dto;

import com.testpulse.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class UserSubscriptionResponse {
    Long userId;
    SubscriptionStatus status;
    String planCode;
    LocalDateTime expiry;
    Long classId;
    String className;
    boolean hasUsedTrial;
}