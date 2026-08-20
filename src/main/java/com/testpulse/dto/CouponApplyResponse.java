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
public class CouponApplyResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("valid")
    private boolean valid;

    @JsonProperty("message")
    private String message;

    @JsonProperty("couponDetails")
    private CouponDetails couponDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CouponDetails {
        @JsonProperty("code")
        private String code;

        @JsonProperty("discountType")
        private String discountType;

        @JsonProperty("discountValue")
        private int discountValue;

        @JsonProperty("originalAmountInPaise")
        private long originalAmountInPaise;

        @JsonProperty("discountAmountInPaise")
        private long discountAmountInPaise;

        @JsonProperty("finalAmountInPaise")
        private long finalAmountInPaise;

        @JsonProperty("displayFinalPrice")
        private String displayFinalPrice;
    }
}
