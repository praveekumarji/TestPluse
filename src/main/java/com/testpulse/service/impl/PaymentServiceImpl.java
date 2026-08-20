package com.testpulse.service.impl;

import com.testpulse.model.Payment;
import com.testpulse.model.PaymentStatus;
import com.testpulse.model.User;
import com.testpulse.repository.PaymentRepository;
import com.testpulse.repository.UserRepository;
import com.testpulse.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Payment initiatePayment(Long userId, String planName, BigDecimal amount) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException("Plan name is required.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        User user = userRepository == null ? null : userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Payment payment = Payment.builder()
                .user(user)
                .planName(planName)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    @Override
    @Transactional
    public Payment recordSuccess(Long paymentId, String razorpayPaymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setPaidAt(LocalDateTime.now());
        payment.setFailureReason(null);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment recordFailure(Long paymentId, String failureReason) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failureReason == null || failureReason.isBlank()
                ? "Payment failed or was cancelled by the user."
                : failureReason);
        payment.setPaidAt(null);
        return paymentRepository.save(payment);
    }
}
