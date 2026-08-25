package com.testpulse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidationRequest {
    @JsonProperty("couponCode")
    private String couponCode;

    @JsonProperty("planId")
    private String planId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("currentAmountInPaise")
    private long currentAmountInPaise;
}
