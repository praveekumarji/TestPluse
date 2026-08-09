/*
package com.testpulse.controller;

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
    public ResponseEntity<User> register(@RequestParam String email, @RequestParam String password, @RequestParam String fullName) {
        User user = userService.registerUser(email, password, fullName);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        // Implement login logic
        return ResponseEntity.ok("JWT Token");
    }
}
*/
