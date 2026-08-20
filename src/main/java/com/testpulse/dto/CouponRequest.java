package com.testpulse.dto;

import com.testpulse.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequest {
    private String code;
    private String title;
    private DiscountType discountType;
    private long discountValue;
    private long minimumAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean active;
    private Integer maxUses;
    private String applicablePlanId;
}
