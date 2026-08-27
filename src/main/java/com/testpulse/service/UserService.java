package com.testpulse.service;

import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(String mobileNumber, String password, String fullName);

    User registerUser(String email, String mobileNumber, String password, String fullName, String preferredLanguage);

    User registerUser(String email, String mobileNumber, String password, String fullName,
                      String preferredLanguage, String deviceHash);

    User registerUser(String email, String password, String fullName, String preferredLanguage);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    Optional<User> findById(Long id);

    User updatePreferredLanguage(Long userId, String language);

    User updateProfile(Long userId, String fullName, String email, String mobileNumber, String preferredLanguage);

    User updateSubscriptionStatus(Long userId, SubscriptionStatus status);

    void changePassword(Long userId, String currentPassword, String newPassword);

    Optional<User> login(String mobileNumber, String password);
}
