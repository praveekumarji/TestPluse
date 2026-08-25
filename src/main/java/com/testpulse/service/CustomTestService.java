package com.testpulse.service;

import com.testpulse.dto.CreateCustomTestRequest;
import com.testpulse.dto.CustomTestResponse;
import com.testpulse.dto.CustomTestPreviewResponse;
import com.testpulse.dto.QuestionResponse;

import java.util.List;

public interface CustomTestService {
    CustomTestPreviewResponse generateCustomTest(String subject, int questionCount,
                                                 String difficulty, String mode, String lang);

    List<CustomTestResponse> getMyCustomTests(String lang);

    CustomTestResponse getCustomTestById(Long id, String lang);

    CustomTestResponse createCustomTest(CreateCustomTestRequest request, String lang);

    void deleteCustomTest(Long id);

    List<QuestionResponse> getQuestionsForCustomTest(Long id, String lang);
}
