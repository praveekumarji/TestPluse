package com.testpulse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {
    @NotNull
    private Long testId;

    @NotBlank
    private String subject;

    private String subjectHi;

    @NotBlank
    private String text;

    private String textHi;

    @NotNull
    @Size(min = 2)
    private List<String> options;

    private List<String> optionsHi;

    @Min(0)
    private int correctOptionIndex;

    private String explanation;
    private String explanationHi;
    private String hint;
    private String hintHi;
}
