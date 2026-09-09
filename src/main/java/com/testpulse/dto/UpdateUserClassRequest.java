package com.testpulse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserClassRequest {
    @NotNull
    private Long classId;
}