package com.testpulse.controller;

import com.testpulse.dto.CreateUserRequest;
import com.testpulse.model.User;
import com.testpulse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(defaultValue = "en") String preferredLanguage) {
        User user = userService.registerUser(email, password, fullName, preferredLanguage);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("User payload cannot be null.");
            }
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email is required.");
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required.");
            }
            if (request.getFullName() == null || request.getFullName().isBlank()) {
                throw new IllegalArgumentException("Full name is required.");
            }

            User user = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullName(),
                    request.getPreferredLanguage() == null ? "en" : request.getPreferredLanguage()
            );
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok("JWT Token");
    }
}

