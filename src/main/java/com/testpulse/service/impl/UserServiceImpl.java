package com.testpulse.service.impl;

import com.testpulse.model.User;
import com.testpulse.repository.UserRepository;
import com.testpulse.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(String email, String password, String fullName) {
        return registerUser(email, password, fullName, "en");
    }

    @Override
    public User registerUser(String email, String password, String fullName, String preferredLanguage) {
        User user = User.builder()
                .email(email)
                .passwordHash(password)
                .fullName(fullName)
                .createdAt(LocalDateTime.now())
                .preferredLanguage(normalizeLanguage(preferredLanguage))
                .build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updatePreferredLanguage(Long userId, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPreferredLanguage(normalizeLanguage(language));
        return userRepository.save(user);
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
}

