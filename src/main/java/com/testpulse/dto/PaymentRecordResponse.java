package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordResponse {
    private boolean success;
    private String message;
    private String paymentStatus;
    private UserSubscriptionResponse user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSubscriptionResponse {
        private Long id;
        private String subscriptionStatus;
        private String subscriptionPlan;
    }
}