package com.testpulse.service.impl;

import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.repository.TestRepository;
import com.testpulse.service.TestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public List<Test> getAllTests(String searchQuery, String subject) {
        // Implement filtering logic based on searchQuery and subject
        return testRepository.findAll();
    }

    @Override
    public Test getTestById(Long id) {
        return testRepository.findById(id).orElseThrow(() -> new RuntimeException("Test not found"));
    }

    @Override
    public Test createCustomTest(String subject, int questionCount, String difficulty, String mode) {
        // Implement custom test creation logic
        return null;
    }

    @Override
    public List<Test> addTest(List<Test> tests) {
        testRepository.saveAll(tests);
        return tests;
    }


}
