package com.testpulse.service.impl;

import com.testpulse.model.Payment;
import com.testpulse.model.PaymentStatus;
import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;
import com.testpulse.dto.PaymentRecordRequest;
import com.testpulse.dto.PaymentRecordResponse;
import com.testpulse.repository.PaymentRepository;
import com.testpulse.repository.UserRepository;
import com.testpulse.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Locale;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final String razorpayKeySecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              UserRepository userRepository,
                              @Value("${razorpay.key-secret:}") String razorpayKeySecret) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.razorpayKeySecret = razorpayKeySecret;
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

    @Override
    @Transactional
    public PaymentRecordResponse recordPayment(PaymentRecordRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (request.getAmountInPaise() == null || request.getAmountInPaise() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (request.getDurationDays() == null || request.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Duration days must be greater than zero.");
        }
        if (request.getPlanId() == null || request.getPlanId().isBlank()) {
            throw new IllegalArgumentException("Plan ID is required.");
        }

        String requestedStatus = request.getPaymentStatus().trim().toUpperCase(Locale.ROOT);
        PaymentStatus storedStatus = switch (requestedStatus) {
            case "INITIATED" -> PaymentStatus.PENDING;
            case "SUCCESS" -> PaymentStatus.SUCCESS;
            case "FAILED" -> PaymentStatus.FAILED;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            default -> throw new IllegalArgumentException("paymentStatus must be INITIATED, SUCCESS, FAILED, or CANCELLED.");
        };

        if (storedStatus == PaymentStatus.SUCCESS) {
            verifyRazorpaySignature(request);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Payment payment = Payment.builder()
                .user(user)
                .planName(request.getPlanTitle() == null || request.getPlanTitle().isBlank()
                        ? request.getPlanId() : request.getPlanTitle())
                .planId(request.getPlanId())
                .planCode(request.getPlanCode())
                .planTitle(request.getPlanTitle())
                .durationDays(request.getDurationDays())
                .originalAmountInPaise(request.getOriginalAmountInPaise())
                .discountAmountInPaise(request.getDiscountAmountInPaise())
                .amountInPaise(request.getAmountInPaise())
                .couponCode(request.getCouponCode())
                .amount(BigDecimal.valueOf(request.getAmountInPaise(), 2))
                .currency(request.getCurrency() == null ? "INR" : request.getCurrency())
                .status(storedStatus)
                .razorpayPaymentId(request.getRazorpayPaymentId())
                .razorpayOrderId(request.getRazorpayOrderId())
                .razorpaySignature(request.getRazorpaySignature())
                .failureReason(request.getErrorMessage())
                .createdAt(LocalDateTime.now())
                .paidAt(storedStatus == PaymentStatus.SUCCESS ? LocalDateTime.now() : null)
                .build();
        paymentRepository.save(payment);

        if (storedStatus == PaymentStatus.SUCCESS) {
            user.setSubscriptionStatus(SubscriptionStatus.PAID);
            user.setSubscriptionPlan(request.getPlanCode() == null ? request.getPlanId() : request.getPlanCode());
            user.setSubscriptionExpiry(LocalDateTime.now().plusDays(request.getDurationDays()));
            userRepository.save(user);
        }

        return PaymentRecordResponse.builder()
                .success(true)
                .message("Payment record stored successfully")
                .paymentStatus(requestedStatus)
                .user(PaymentRecordResponse.UserSubscriptionResponse.builder()
                        .id(user.getId())
                        .subscriptionStatus(user.getSubscriptionStatus().name())
                        .subscriptionPlan(user.getSubscriptionPlan())
                        .build())
                .build();
    }

    private void verifyRazorpaySignature(PaymentRecordRequest request) {
        if (razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            throw new IllegalStateException("Razorpay key secret is not configured.");
        }
        if (request.getRazorpayOrderId() == null || request.getRazorpayOrderId().isBlank()
                || request.getRazorpayPaymentId() == null || request.getRazorpayPaymentId().isBlank()
                || request.getRazorpaySignature() == null || request.getRazorpaySignature().isBlank()) {
            throw new IllegalArgumentException("Razorpay order ID, payment ID, and signature are required for successful payments.");
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal((request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId())
                    .getBytes(StandardCharsets.UTF_8));
            String expectedSignature = java.util.HexFormat.of().formatHex(digest);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
                    request.getRazorpaySignature().trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Invalid Razorpay payment signature.");
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to verify Razorpay payment signature.", ex);
        }
    }
}
