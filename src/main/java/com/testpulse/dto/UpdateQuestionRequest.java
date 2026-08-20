package com.testpulse.dto;

import jakarta.validation.constraints.Min;
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
public class UpdateQuestionRequest {
    @Size(min = 1)
    private String subject;

    @Size(min = 1)
    private String subjectHi;

    @Size(min = 1)
    private String text;

    @Size(min = 1)
    private String textHi;

    @Size(min = 2)
    private List<String> options;

    private List<String> optionsHi;

    @Min(0)
    private Integer correctOptionIndex;

    @Size(min = 1)
    private String explanation;

    @Size(min = 1)
    private String explanationHi;

    @Size(min = 1)
    private String hint;

    @Size(min = 1)
    private String hintHi;

    private Boolean active;
}
