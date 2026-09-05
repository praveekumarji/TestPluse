package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMobileNumberRequest {
    @NotBlank
    private String mobileNumber;
}