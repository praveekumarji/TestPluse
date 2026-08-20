package com.testpulse.controller;

import com.testpulse.dto.PaymentFailureRequest;
import com.testpulse.dto.PaymentInitiateRequest;
import com.testpulse.dto.PaymentSuccessRequest;
import com.testpulse.model.Payment;
import com.testpulse.model.PaymentStatus;
import com.testpulse.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentInitiateRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Payment payload is required.");
            }

            Payment payment = paymentService.initiatePayment(
                    request.getUserId(),
                    request.getPlanName(),
                    request.getAmount()
            );

            return ResponseEntity.ok(Map.of(
                    "paymentId", payment.getId(),
                    "userId", payment.getUser() != null ? payment.getUser().getId() : null,
                    "planName", payment.getPlanName(),
                    "amount", payment.getAmount(),
                    "status", payment.getStatus().name(),
                    "createdAt", payment.getCreatedAt()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/record-success")
    public ResponseEntity<?> recordSuccess(@RequestBody PaymentSuccessRequest request) {
        try {
            Payment payment = paymentService.recordSuccess(request.getPaymentId(), request.getRazorpayPaymentId());
            return ResponseEntity.ok(Map.of(
                    "paymentId", payment.getId(),
                    "status", payment.getStatus().name(),
                    "razorpayPaymentId", payment.getRazorpayPaymentId(),
                    "paidAt", payment.getPaidAt()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/record-failure")
    public ResponseEntity<?> recordFailure(@RequestBody PaymentFailureRequest request) {
        try {
            Payment payment = paymentService.recordFailure(request.getPaymentId(), request.getReason());
            return ResponseEntity.ok(Map.of(
                    "paymentId", payment.getId(),
                    "status", payment.getStatus().name(),
                    "failureReason", payment.getFailureReason()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
