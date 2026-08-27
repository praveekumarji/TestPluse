package com.testpulse.model;

import com.testpulse.model.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String mobileNumber;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(name = "auth_provider", length = 30)
    @Builder.Default
    private String authProvider = "LOCAL";

    @Column(name = "target_exam")
    private String targetExam;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String preferredLanguage = "en";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.FREE;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "subscription_expiry")
    private LocalDateTime subscriptionExpiry;

    @Column(name = "has_used_trial", nullable = false)
    @Builder.Default
    private boolean hasUsedTrial = false;

    @Column(name = "google_subject", unique = true)
    private String googleSubject;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
}
