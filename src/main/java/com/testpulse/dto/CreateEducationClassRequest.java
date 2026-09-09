package com.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEducationClassRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}