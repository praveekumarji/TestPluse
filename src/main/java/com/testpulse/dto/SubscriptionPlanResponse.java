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
public class SubscriptionPlanResponse {
    private String id;
    private String planCode;
    private String title;
    private String subtitle;
    private int durationDays;
    private long originalAmountInPaise;
    private long discountedAmountInPaise;
    private String displayPrice;
    private String displayOriginalPrice;
    private int discountPercentage;
    private String badge;
    private boolean isRecommended;
    private List<String> features;
}
