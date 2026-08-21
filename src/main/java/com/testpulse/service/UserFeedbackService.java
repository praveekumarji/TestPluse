package com.testpulse.service;

import com.testpulse.dto.UserFeedbackRequest;
import com.testpulse.dto.UserFeedbackResponse;
import com.testpulse.model.UserFeedback;
import com.testpulse.repository.UserFeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UserFeedbackService {
    private final UserFeedbackRepository feedbackRepository;

    public UserFeedbackService(UserFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public UserFeedbackResponse createFeedback(UserFeedbackRequest request) {
        String feedbackId = "fb_" + UUID.randomUUID().toString().replace("-", "");
        UserFeedback feedback = UserFeedback.builder()
                .feedbackId(feedbackId)
                .userId(request.getUserId().trim())
                .userEmail(request.getUserEmail().trim().toLowerCase())
                .category(request.getCategory())
                .rating(request.getRating())
                .message(request.getMessage().trim())
                .appVersion(request.getAppVersion())
                .deviceInfo(toDeviceInfo(request.getDeviceInfo()))
                .context(toFeedbackContext(request.getContext()))
                .createdAt(request.getCreatedAt() == null ? OffsetDateTime.now() : request.getCreatedAt())
                .build();

        feedbackRepository.save(feedback);
        return new UserFeedbackResponse(true, feedbackId, "Thank you for your feedback!");
    }

    private UserFeedback.DeviceInfo toDeviceInfo(UserFeedbackRequest.DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return null;
        }
        return UserFeedback.DeviceInfo.builder()
                .osVersion(deviceInfo.getOsVersion())
                .deviceModel(deviceInfo.getDeviceModel())
                .build();
    }

    private UserFeedback.FeedbackContext toFeedbackContext(UserFeedbackRequest.FeedbackContext context) {
        if (context == null) {
            return null;
        }
        return UserFeedback.FeedbackContext.builder()
                .testId(context.getTestId())
                .questionId(context.getQuestionId())
                .subject(context.getSubject())
                .build();
    }
}