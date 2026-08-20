package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private Long id;
    private Long testId;
    private String subject;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private String hint;
    private String hintHi;
}
