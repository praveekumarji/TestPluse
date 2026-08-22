package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRecordRequest {
    @NotNull
    private Long userId;

    private String userMobile;
    private String userEmail;
    private String userName;

    @NotBlank
    private String planId;

    private String planCode;
    private String planTitle;

    @NotNull
    private Integer durationDays;

    private Long originalAmountInPaise;
    private Long discountAmountInPaise;
    @NotNull
    private Long amountInPaise;

    private String couponCode;

    private String currency;

    @NotBlank
    private String paymentStatus;

    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    private String errorMessage;
}