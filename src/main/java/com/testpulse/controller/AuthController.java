package com.testpulse.controller;

import com.testpulse.dto.AuthResponse;
import com.testpulse.dto.CreateUserRequest;
import com.testpulse.dto.UserResponse;
import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;
import com.testpulse.service.UserService;
import com.testpulse.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam(required = false) String email,
            @RequestParam String mobileNumber,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(defaultValue = "en") String preferredLanguage) {
        try {
            User user = userService.registerUser(email, mobileNumber, password, fullName, preferredLanguage);
            String token = JwtUtil.generateToken(user.getId(), user.getMobileNumber());
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .user(toUserResponse(user))
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("User payload cannot be null.");
            }
            if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                    (request.getMobileNumber() == null || request.getMobileNumber().isBlank())) {
                throw new IllegalArgumentException("Either email or mobile number is required.");
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required.");
            }
            if (request.getFullName() == null || request.getFullName().isBlank()) {
                throw new IllegalArgumentException("Full name is required.");
            }

            String subscriptionStatus = request.getSubscriptionStatus() == null ? "FREE" : request.getSubscriptionStatus();
            if (!"FREE".equalsIgnoreCase(subscriptionStatus) && !"PAID".equalsIgnoreCase(subscriptionStatus)) {
                throw new IllegalArgumentException("subscriptionStatus must be FREE or PAID.");
            }

            User user = userService.registerUser(
                    request.getEmail(),
                    request.getMobileNumber(),
                    request.getPassword(),
                    request.getFullName(),
                    request.getPreferredLanguage() == null ? "en" : request.getPreferredLanguage()
            );

            user.setSubscriptionStatus("PAID".equalsIgnoreCase(subscriptionStatus)
                    ? SubscriptionStatus.PAID
                    : SubscriptionStatus.FREE);
            String token = JwtUtil.generateToken(user.getId(), user.getMobileNumber());
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .user(toUserResponse(user))
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String mobileNumber, @RequestParam String password) {
        try {
            Optional<User> user = userService.login(mobileNumber, password);
            if (user.isPresent()) {
                String token = JwtUtil.generateToken(user.get().getId(), user.get().getMobileNumber());
                return ResponseEntity.ok(AuthResponse.builder()
                        .token(token)
                        .user(toUserResponse(user.get()))
                        .build());
            }
            return ResponseEntity.status(401).body("Invalid mobile number or password.");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of(
                "message", "Logout successful",
                "status", "SUCCESS"
        ));
    }

    private UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .fullName(user.getFullName())
                .preferredLanguage(user.getPreferredLanguage())
                .subscriptionStatus(user.getSubscriptionStatus())
                .build();
    }
}

