package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String email;
    private String fullName;
    private String googleId;

    private String avatarUrl;

    @NotBlank
    private String idToken;

    private String mobileNumber;
    private String deviceHash;
    private String preferredLanguage = "hi";
    private String targetExam;
}