package com.testpulse.service;

import com.testpulse.model.Payment;
import com.testpulse.dto.PaymentRecordRequest;
import com.testpulse.dto.PaymentRecordResponse;

import java.math.BigDecimal;

public interface PaymentService {
    Payment initiatePayment(Long userId, String planName, BigDecimal amount);

    Payment getPaymentById(Long paymentId);

    Payment recordSuccess(Long paymentId, String razorpayPaymentId);

    Payment recordFailure(Long paymentId, String failureReason);

    PaymentRecordResponse recordPayment(PaymentRecordRequest request);
}
