package com.testpulse.controller;

import com.testpulse.dto.UpdateMobileNumberRequest;
import com.testpulse.dto.UpdateUserProfileRequest;
import com.testpulse.dto.UserResponse;
import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;
import com.testpulse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email-exists")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        return ResponseEntity.ok(Map.of(
                "email", normalizedEmail,
                "exists", userService.existsByEmail(email)
        ));
    }

    @PutMapping("/{id}/language")
    public ResponseEntity<UserResponse> updatePreferredLanguage(@PathVariable Long id,
                                                              @RequestParam String language) {
        User user = userService.updatePreferredLanguage(id, language);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PatchMapping("/mobile-number")
    public ResponseEntity<?> updateMobileNumber(
            Authentication authentication,
            @Valid @RequestBody UpdateMobileNumberRequest request) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            User user = userService.updateMobileNumber(userId, request.getMobileNumber());
            return ResponseEntity.ok(toUserResponse(user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "MOBILE_NUMBER_ALREADY_REGISTERED",
                    "message", "This mobile number is already registered to another user."
            ));
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id,
                                                     @RequestBody UpdateUserProfileRequest request) {
        try {
            User user = userService.updateProfile(
                    id,
                    request.getFullName(),
                    request.getEmail(),
                    request.getMobileNumber(),
                    request.getPreferredLanguage()
            );
            return ResponseEntity.ok(toUserResponse(user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/subscription")
    public ResponseEntity<UserResponse> updateSubscriptionStatus(@PathVariable Long id,
                                                               @RequestParam String subscriptionStatus) {
        try {
            SubscriptionStatus status = SubscriptionStatus.valueOf(subscriptionStatus.trim().toUpperCase());
            User user = userService.updateSubscriptionStatus(id, status);
            return ResponseEntity.ok(toUserResponse(user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
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
                .subscriptionPlan(user.getSubscriptionPlan())
                .subscriptionExpiry(user.getSubscriptionExpiry())
                .hasUsedTrial(user.isHasUsedTrial())
                .build();
    }
}
