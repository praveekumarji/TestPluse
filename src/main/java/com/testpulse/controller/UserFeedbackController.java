package com.testpulse.controller;

import com.testpulse.dto.UserFeedbackRequest;
import com.testpulse.dto.UserFeedbackResponse;
import com.testpulse.service.UserFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {
    private final UserFeedbackService feedbackService;

    public UserFeedbackController(UserFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<UserFeedbackResponse> createFeedback(
            @Valid @RequestBody UserFeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.createFeedback(request));
    }
}