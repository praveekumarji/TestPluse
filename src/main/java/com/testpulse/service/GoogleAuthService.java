package com.testpulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpulse.dto.AuthResponse;
import com.testpulse.dto.GoogleAuthRequest;
import com.testpulse.dto.UserResponse;
import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.TrialDevice;
import com.testpulse.model.User;
import com.testpulse.repository.TrialDeviceRepository;
import com.testpulse.repository.UserRepository;
import com.testpulse.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class GoogleAuthService {
    private static final String TRIAL_PLAN = "TRIAL_3_DAY";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final UserRepository userRepository;
    private final TrialDeviceRepository trialDeviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final WelcomeEmailService welcomeEmailService;
    private final int trialDurationDays;

    public GoogleAuthService(UserRepository userRepository,
                             TrialDeviceRepository trialDeviceRepository,
                             PasswordEncoder passwordEncoder,
                             WelcomeEmailService welcomeEmailService,
                             @Value("${subscription.trial-days:3}") int trialDurationDays) {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();
        this.userRepository = userRepository;
        this.trialDeviceRepository = trialDeviceRepository;
        this.passwordEncoder = passwordEncoder;
        this.welcomeEmailService = welcomeEmailService;
        this.trialDurationDays = trialDurationDays;
    }

    @Transactional
    public AuthResponse authenticate(GoogleAuthRequest request) {
        GoogleTokenInfo tokenInfo = verify(request.getIdToken());
        String email = normalizeEmail(tokenInfo.email());
        if (email == null || !tokenInfo.emailVerified()) {
            throw new IllegalArgumentException("Google account email must be verified.");
        }

        if (request.getEmail() != null && !email.equals(normalizeEmail(request.getEmail()))) {
            throw new IllegalArgumentException("Request email does not match the verified Google email.");
        }
        if (request.getGoogleId() != null && !tokenInfo.subject().equals(request.getGoogleId())) {
            throw new IllegalArgumentException("Request Google ID does not match the verified token.");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        boolean isNewUser = user == null;
        if (user == null) {
            user = createUser(tokenInfo, email, request);
        } else {
            if (user.getGoogleSubject() != null && !user.getGoogleSubject().equals(tokenInfo.subject())) {
                throw new IllegalArgumentException("This email is linked to another Google account.");
            }
            user.setGoogleSubject(tokenInfo.subject());
            String displayName = tokenInfo.name();
            if (displayName != null && !displayName.isBlank()) {
                user.setFullName(displayName);
            }
            String avatarUrl = tokenInfo.picture();
            if (avatarUrl != null && !avatarUrl.isBlank()) {
                user.setAvatarUrl(avatarUrl);
            }
            if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()
                    && (user.getMobileNumber() == null || user.getMobileNumber().isBlank())) {
                user.setMobileNumber(request.getMobileNumber().trim());
            }
            user.setAuthProvider("GOOGLE");
            user.setEmailVerified(true);
            if (request.getTargetExam() != null && !request.getTargetExam().isBlank()) {
                user.setTargetExam(request.getTargetExam().trim());
            }
            expireTrialIfNeeded(user);
            user.setLastLoginAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        return AuthResponse.builder()
            .success(true)
            .message("Authentication successful")
            .isNewUser(isNewUser)
                .token(JwtUtil.generateToken(user.getId(), user.getMobileNumber(), user.getRole().name()))
                .user(toUserResponse(user))
                .build();
    }

    private User createUser(GoogleTokenInfo tokenInfo, String email, GoogleAuthRequest request) {
        String deviceHash = normalizeDeviceHash(request.getDeviceHash());
        boolean trialAvailable = deviceHash != null && !trialDeviceRepository.existsById(deviceHash);
        LocalDateTime now = LocalDateTime.now();
        String displayName = tokenInfo.name();
        String avatarUrl = tokenInfo.picture();
        String fallbackName = (displayName == null || displayName.isBlank())
                ? (request.getFullName() == null || request.getFullName().isBlank() ? email : request.getFullName().trim())
                : displayName;
        String temporaryPassword = generateTemporaryPassword(fallbackName);

        User user = User.builder()
                .email(email)
            .mobileNumber(request.getMobileNumber() == null || request.getMobileNumber().isBlank()
                ? null : request.getMobileNumber().trim())
                .passwordHash(passwordEncoder.encode(temporaryPassword))
            .fullName(fallbackName)
            .avatarUrl(avatarUrl == null || avatarUrl.isBlank() ? request.getAvatarUrl() : avatarUrl)
            .isEmailVerified(true)
            .authProvider("GOOGLE")
            .targetExam(request.getTargetExam())
                .createdAt(now)
                .preferredLanguage(request.getPreferredLanguage() == null ? "en" : request.getPreferredLanguage())
                .subscriptionStatus(trialAvailable ? SubscriptionStatus.TRIAL : SubscriptionStatus.FREE)
                .subscriptionPlan(trialAvailable ? TRIAL_PLAN : null)
                .subscriptionExpiry(trialAvailable ? now.plusDays(trialDurationDays) : null)
                .hasUsedTrial(trialAvailable)
                .googleSubject(tokenInfo.subject())
                .lastLoginAt(now)
                .build();
        User saved = userRepository.save(user);
        if (trialAvailable) {
            trialDeviceRepository.save(TrialDevice.builder()
                    .deviceHash(deviceHash)
                    .firstUserId(saved.getId())
                    .trialUsedAt(now)
                    .build());
        }
        welcomeEmailService.sendWelcomeEmailAsync(saved, temporaryPassword);
        return saved;
    }

    private GoogleTokenInfo verify(String idToken) {
        try {
            log.info("Verifying Google ID token: {}", idToken);
            String encodedToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + encodedToken))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalArgumentException("Invalid Google ID token.");
            }
            JsonNode payload = objectMapper.readTree(response.body());
            String subject = payload.path("sub").asText(null);
            String email = payload.path("email").asText(null);
            String emailVerified = payload.path("email_verified").asText("");
            log.info("Google ID token payload: subject={}, email={}, email_verified={}", subject, email, emailVerified);

            validateGoogleTokenPayload(payload);

            long exp = payload.path("exp").asLong(-1L);
            log.info("Google ID token expiration timestamp: {}", exp > 0 ? exp : "not provided");
            return new GoogleTokenInfo(
                    subject,
                    email,
                    "true".equalsIgnoreCase(emailVerified),
                    payload.path("name").asText(null),
                    payload.path("picture").asText(null)
            );
        } catch (Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) ex;
            }
            throw new IllegalArgumentException("Invalid Google ID token.", ex);
        }
    }

    public static void validateGoogleTokenPayload(JsonNode payload) {
        String subject = payload.path("sub").asText(null);
        String email = payload.path("email").asText(null);
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Invalid Google ID token: missing subject.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Invalid Google ID token: missing email.");
        }

        long exp = payload.path("exp").asLong(-1L);
        if (exp > 0 && exp <= Instant.now().getEpochSecond()) {
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }

        long expiresIn = payload.path("expires_in").asLong(-1L);
        if (expiresIn == 0) {
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }
    }

    private record GoogleTokenInfo(String subject, String email, boolean emailVerified,
                                   String name, String picture) {
    }

    private void expireTrialIfNeeded(User user) {
        if (user.getSubscriptionStatus() == SubscriptionStatus.TRIAL
                && user.getSubscriptionExpiry() != null
                && !user.getSubscriptionExpiry().isAfter(LocalDateTime.now())) {
            user.setSubscriptionStatus(SubscriptionStatus.FREE);
            user.setSubscriptionPlan(null);
            user.setSubscriptionExpiry(null);
        }
    }

    private UserResponse toUserResponse(User user) {
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

    private String generateTemporaryPassword(String userName) {
        String cleanName = userName == null ? "user" : userName.trim();
        cleanName = cleanName.replaceAll("[^a-zA-Z0-9]", "");
        if (cleanName.length() > 12) {
            cleanName = cleanName.substring(0, 12);
        }
        if (cleanName.isBlank()) {
            cleanName = "user";
        }
        int number = ThreadLocalRandom.current().nextInt(100, 1000);
        return cleanName + number;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeDeviceHash(String deviceHash) {
        return deviceHash == null || deviceHash.isBlank()
                ? null : deviceHash.trim().toLowerCase(Locale.ROOT);
    }
}