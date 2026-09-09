package com.testpulse.controller;

import com.testpulse.dto.UpdateMobileNumberRequest;
import com.testpulse.dto.UpdateUserProfileRequest;
import com.testpulse.dto.UpdateUserClassRequest;
import com.testpulse.dto.UserResponse;
import com.testpulse.dto.UserSubscriptionResponse;
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

    @GetMapping("/me/subscription")
    public ResponseEntity<?> getMySubscription(Authentication authentication) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            return userService.findById(userId)
                    .map(user -> ResponseEntity.ok(UserSubscriptionResponse.builder()
                            .userId(user.getId())
                            .status(user.getEffectiveSubscriptionStatus())
                            .planCode(user.getSubscriptionPlan())
                            .expiry(user.getSubscriptionExpiry())
                            .classId(user.getEducationClass() == null ? null : user.getEducationClass().getId())
                            .className(user.getEducationClass() == null ? null : user.getEducationClass().getName())
                            .hasUsedTrial(user.isHasUsedTrial())
                            .build()))
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException | NullPointerException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required."));
        }
    }

    @PatchMapping("/me/class")
    public ResponseEntity<?> updateMyClass(Authentication authentication,
                                            @Valid @RequestBody UpdateUserClassRequest request) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            User user = userService.updateClass(userId, request.getClassId());
            return ResponseEntity.ok(toUserResponse(user));
        } catch (NumberFormatException | NullPointerException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/class")
    public ResponseEntity<?> updateUserClass(@PathVariable Long id,
                                              Authentication authentication,
                                              @Valid @RequestBody UpdateUserClassRequest request) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        try {
            if (authentication == null || (!isAdmin && !String.valueOf(id).equals(authentication.getName()))) {
                return ResponseEntity.status(403).body(Map.of("error", "You cannot update this user's class."));
            }
            User user = userService.updateClass(id, request.getClassId());
            return ResponseEntity.ok(toUserResponse(user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
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
                    request.getPreferredLanguage(),
                    request.getClassId()
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
                .classId(user.getEducationClass() == null ? null : user.getEducationClass().getId())
                .className(user.getEducationClass() == null ? null : user.getEducationClass().getName())
                .preferredLanguage(user.getPreferredLanguage())
                .subscriptionStatus(user.getEffectiveSubscriptionStatus())
                .subscriptionPlan(user.getSubscriptionPlan())
                .subscriptionExpiry(user.getSubscriptionExpiry())
                .hasUsedTrial(user.isHasUsedTrial())
                .build();
    }
}
