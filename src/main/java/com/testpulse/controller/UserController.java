package com.testpulse.controller;

import com.testpulse.model.User;
import com.testpulse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/language")
    public ResponseEntity<User> updatePreferredLanguage(@PathVariable Long id,
                                                      @RequestParam String language) {
        User user = userService.updatePreferredLanguage(id, language);
        return ResponseEntity.ok(user);
    }
}
