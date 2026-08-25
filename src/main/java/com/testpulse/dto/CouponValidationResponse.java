package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidationResponse {
    private boolean valid;
    private String message;
    private String code;
    private long discountAmount;
    private long finalAmount;
    private String planId;
}
