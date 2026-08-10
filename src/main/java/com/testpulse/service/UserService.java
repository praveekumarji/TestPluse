package com.testpulse.service;

import com.testpulse.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(String email, String password, String fullName);

    User registerUser(String email, String password, String fullName, String preferredLanguage);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User updatePreferredLanguage(Long userId, String language);
}
