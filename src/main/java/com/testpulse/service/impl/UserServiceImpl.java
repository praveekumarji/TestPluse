package com.testpulse.service.impl;

import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;
import com.testpulse.repository.UserRepository;
import com.testpulse.repository.TrialDeviceRepository;
import com.testpulse.model.TrialDevice;
import com.testpulse.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final String TRIAL_PLAN = "TRIAL_3_DAY";

    private final UserRepository userRepository;
    private final TrialDeviceRepository trialDeviceRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final int trialDurationDays;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           TrialDeviceRepository trialDeviceRepository,
                           @Value("${subscription.trial-days:3}") int trialDurationDays) {
        this.userRepository = userRepository;
        this.trialDeviceRepository = trialDeviceRepository;
        this.trialDurationDays = trialDurationDays;
    }

    public UserServiceImpl(UserRepository userRepository) {
        this(userRepository, null, 3);
    }

    @Override
    public User registerUser(String mobileNumber, String password, String fullName) {
        return registerUser(null, mobileNumber, password, fullName, "en");
    }

    @Override
    public User registerUser(String email, String mobileNumber, String password, String fullName, String preferredLanguage) {
        return registerUser(email, mobileNumber, password, fullName, preferredLanguage, null);
    }

    @Override
    public User registerUser(String email, String mobileNumber, String password, String fullName,
                             String preferredLanguage, String deviceHash) {
        String normalizedMobile = normalizeMobileNumber(mobileNumber);

        if (normalizedMobile == null || normalizedMobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number is required.");
        }
        if (userRepository.existsByMobileNumber(normalizedMobile)) {
            throw new IllegalArgumentException("User with this mobile number already exists.");
        }
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String normalizedDeviceHash = normalizeDeviceHash(deviceHash);
        boolean trialAvailable = normalizedDeviceHash != null && trialDeviceRepository != null
            && !trialDeviceRepository.existsById(normalizedDeviceHash);

        User user = User.builder()
                .email(email)
                .mobileNumber(normalizedMobile)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .createdAt(LocalDateTime.now())
                .preferredLanguage(normalizeLanguage(preferredLanguage))
                .subscriptionStatus(trialAvailable ? SubscriptionStatus.TRIAL : SubscriptionStatus.FREE)
                .subscriptionPlan(trialAvailable ? TRIAL_PLAN : null)
                .subscriptionExpiry(trialAvailable ? LocalDateTime.now().plusDays(trialDurationDays) : null)
                .hasUsedTrial(trialAvailable)
                .build();
        User savedUser = userRepository.save(user);
        if (trialAvailable) {
            try {
                trialDeviceRepository.save(TrialDevice.builder()
                        .deviceHash(normalizedDeviceHash)
                        .firstUserId(savedUser.getId())
                        .trialUsedAt(LocalDateTime.now())
                        .build());
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                savedUser.setSubscriptionStatus(SubscriptionStatus.FREE);
                savedUser.setSubscriptionPlan(null);
                savedUser.setSubscriptionExpiry(null);
                savedUser.setHasUsedTrial(false);
                return userRepository.save(savedUser);
            }
        }
        return savedUser;
    }

    @Override
    public User registerUser(String email, String password, String fullName, String preferredLanguage) {
        return registerUser(email, null, password, fullName, preferredLanguage);
    }

    @Override
    @Cacheable(value = "users", key = "'email:' + #email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Cacheable(value = "users", key = "'mobile:' + #mobileNumber")
    public Optional<User> findByMobileNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(normalizeMobileNumber(mobileNumber));
    }

    @Override
    @Cacheable(value = "users", key = "'id:' + #id")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(this::expireTrialIfNeeded);
    }

    @Override
    @CacheEvict(value = "users", key = "'id:' + #userId")
    public User updatePreferredLanguage(Long userId, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPreferredLanguage(normalizeLanguage(language));
        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User updateProfile(Long userId, String fullName, String email, String mobileNumber, String preferredLanguage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }
        if (email != null && !email.isBlank()) {
            if (userRepository.existsByEmail(email) && !email.equalsIgnoreCase(user.getEmail())) {
                throw new IllegalArgumentException("User with this email already exists.");
            }
            user.setEmail(email);
        }
        if (mobileNumber != null && !mobileNumber.isBlank()) {
            String normalizedMobile = normalizeMobileNumber(mobileNumber);
            if (userRepository.existsByMobileNumber(normalizedMobile) && !normalizedMobile.equals(user.getMobileNumber())) {
                throw new IllegalArgumentException("User with this mobile number already exists.");
            }
            user.setMobileNumber(normalizedMobile);
        }
        if (preferredLanguage != null && !preferredLanguage.isBlank()) {
            user.setPreferredLanguage(normalizeLanguage(preferredLanguage));
        }

        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", key = "'id:' + #userId")
    public User updateSubscriptionStatus(Long userId, SubscriptionStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (status == SubscriptionStatus.TRIAL && user.isHasUsedTrial()) {
            throw new IllegalArgumentException("This user has already used the trial.");
        }
        if (status == SubscriptionStatus.TRIAL) {
            user.setHasUsedTrial(true);
            user.setSubscriptionPlan(TRIAL_PLAN);
            user.setSubscriptionExpiry(LocalDateTime.now().plusDays(trialDurationDays));
        }
        user.setSubscriptionStatus(status == null ? SubscriptionStatus.FREE : status);
        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current password is required.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPasswordHash() == null ||
            (!passwordEncoder.matches(currentPassword, user.getPasswordHash()) &&
                !user.getPasswordHash().equals(currentPassword))) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Optional<User> login(String mobileNumber, String password) {
        String normalizedMobile = normalizeMobileNumber(mobileNumber);
        if (normalizedMobile == null || normalizedMobile.isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByMobileNumber(normalizedMobile)
                .filter(user -> user.getPasswordHash() != null &&
                        (passwordEncoder.matches(password, user.getPasswordHash()) ||
                                user.getPasswordHash().equals(password)))
                .map(this::expireTrialIfNeeded);
    }

    private User expireTrialIfNeeded(User user) {
        if (user.getSubscriptionStatus() == SubscriptionStatus.TRIAL
                && user.getSubscriptionExpiry() != null
                && !user.getSubscriptionExpiry().isAfter(LocalDateTime.now())) {
            user.setSubscriptionStatus(SubscriptionStatus.FREE);
            user.setSubscriptionPlan(null);
            user.setSubscriptionExpiry(null);
            userRepository.save(user);
        }
        return user;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "en";
        }

        String normalized = language.trim().toLowerCase(Locale.ROOT);
        if ("hi".equals(normalized) || "hindi".equals(normalized)) {
            return "hi";
        }

        return "en";
    }

    private String normalizeMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            return null;
        }
        return mobileNumber.trim();
    }

    private String normalizeDeviceHash(String deviceHash) {
        if (deviceHash == null || deviceHash.isBlank()) {
            return null;
        }
        return deviceHash.trim().toLowerCase(Locale.ROOT);
    }
}

