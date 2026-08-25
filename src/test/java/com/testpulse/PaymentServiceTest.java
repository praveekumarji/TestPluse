package com.testpulse;

import com.testpulse.model.Payment;
import com.testpulse.model.User;
import com.testpulse.repository.PaymentRepository;
import com.testpulse.repository.UserRepository;
import com.testpulse.service.PaymentService;
import com.testpulse.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceTest {

    @Test
    void shouldTrackPaymentLifecycle() {
        Map<Long, Payment> paymentsById = new HashMap<>();
        Map<Long, User> usersById = new HashMap<>();
        usersById.put(42L, User.builder().id(42L).mobileNumber("9876543210").fullName("Demo User").passwordHash("hash").createdAt(LocalDateTime.now()).build());

        PaymentRepository paymentRepository = (PaymentRepository) Proxy.newProxyInstance(
                PaymentRepository.class.getClassLoader(),
                new Class<?>[]{PaymentRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "save":
                            Payment payment = (Payment) args[0];
                            if (payment.getId() == null) {
                                payment.setId((long) (paymentsById.size() + 1));
                            }
                            paymentsById.put(payment.getId(), payment);
                            return payment;
                        case "findById":
                            return Optional.ofNullable(paymentsById.get((Long) args[0]));
                        default:
                            return null;
                    }
                }
        );

        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "findById":
                            return Optional.ofNullable(usersById.get((Long) args[0]));
                        default:
                            return null;
                    }
                }
        );

        PaymentService paymentService = new PaymentServiceImpl(paymentRepository, userRepository);

        Payment payment = paymentService.initiatePayment(42L, "Premium Plan", new BigDecimal("299.00"));

        assertNotNull(payment);
        assertEquals("PENDING", payment.getStatus().name());
        assertEquals("Premium Plan", payment.getPlanName());

        paymentService.recordSuccess(payment.getId(), "rzp_123");
        assertEquals("SUCCESS", paymentService.getPaymentById(payment.getId()).getStatus().name());
        assertEquals("rzp_123", paymentService.getPaymentById(payment.getId()).getRazorpayPaymentId());

        paymentService.recordFailure(payment.getId(), "User cancelled during checkout");
        assertEquals("FAILED", paymentService.getPaymentById(payment.getId()).getStatus().name());
        assertEquals("User cancelled during checkout", paymentService.getPaymentById(payment.getId()).getFailureReason());
    }
}
