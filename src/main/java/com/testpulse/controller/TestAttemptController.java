package com.testpulse.controller;

import com.testpulse.model.TestAttempt;
import com.testpulse.service.TestAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    public TestAttemptController(TestAttemptService testAttemptService) {
        this.testAttemptService = testAttemptService;
    }

    @PostMapping
    public ResponseEntity<TestAttempt> submitAttempt(@RequestBody TestAttempt testAttempt) {
        TestAttempt savedAttempt = testAttemptService.submitTestAttempt(testAttempt);
        return ResponseEntity.ok(savedAttempt);
    }

    @GetMapping
    public ResponseEntity<List<TestAttempt>> getAllAttempts(@RequestParam Long userId) {
        List<TestAttempt> attempts = testAttemptService.getAllAttemptsByUserId(userId);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestAttempt> getAttemptById(@PathVariable Long id) {
        TestAttempt attempt = testAttemptService.getAttemptById(id);
        return ResponseEntity.ok(attempt);
    }
}
