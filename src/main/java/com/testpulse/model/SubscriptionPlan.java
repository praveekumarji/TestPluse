package com.testpulse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(name = "plan_code", nullable = false, unique = true)
    private String planCode;

    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "original_amount_in_paise", nullable = false)
    private long originalAmountInPaise;

    @Column(name = "discounted_amount_in_paise", nullable = false)
    private long discountedAmountInPaise;

    @Column(name = "display_price")
    private String displayPrice;

    @Column(name = "display_original_price")
    private String displayOriginalPrice;

    @Column(name = "discount_percentage")
    private int discountPercentage;

    @Column
    private String badge;

    @Column(name = "is_recommended")
    @JsonProperty("isRecommended")
    private boolean isRecommended;

    @ElementCollection
    @CollectionTable(name = "subscription_plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;
}
