package com.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomQuestionResponse {
    private Long id;
    private Long testId;
    private String subject;
    private String subjectHi;
    private String topic;
    private String text;
    private String textHi;
    private List<String> options;
    private List<String> optionsHi;
    private int correctOptionIndex;
    private String explanation;
    private String explanationHi;
    private String hint;
    private String hintHi;
}