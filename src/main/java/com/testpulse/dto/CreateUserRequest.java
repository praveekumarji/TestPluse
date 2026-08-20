package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String email;
    private String mobileNumber;
    private String password;
    private String fullName;
    private String preferredLanguage;
    @Builder.Default
    private String subscriptionStatus = "FREE";
}
