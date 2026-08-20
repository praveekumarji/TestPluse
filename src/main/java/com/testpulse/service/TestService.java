package com.testpulse.service;

import com.testpulse.dto.CreateTestRequest;
import com.testpulse.model.Test;

import java.util.List;

public interface TestService {
    default List<Test> getAllTests(String searchQuery, String subject) {
        return getAllTests(searchQuery, subject, "en");
    }

    List<Test> getAllTests(String searchQuery, String subject, String lang);

    default Test getTestById(Long id) {
        return getTestById(id, "en");
    }

    Test getTestById(Long id, String lang);

    default Test createCustomTest(String subject, int questionCount, String difficulty, String mode) {
        return createCustomTest(subject, questionCount, difficulty, mode, "en");
    }

    Test createCustomTest(String subject, int questionCount, String difficulty, String mode, String lang);

    List<Test> addTest(List<Test> tests);

    List<Test> addTestsFromDto(List<CreateTestRequest> requests);
}