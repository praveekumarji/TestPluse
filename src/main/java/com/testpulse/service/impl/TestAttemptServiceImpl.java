package com.testpulse.service.impl;

import com.testpulse.model.TestAttempt;
import com.testpulse.repository.TestAttemptRepository;
import com.testpulse.service.TestAttemptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestAttemptServiceImpl implements TestAttemptService {

    private final TestAttemptRepository testAttemptRepository;

    public TestAttemptServiceImpl(TestAttemptRepository testAttemptRepository) {
        this.testAttemptRepository = testAttemptRepository;
    }

    @Override
    public TestAttempt submitTestAttempt(TestAttempt testAttempt) {
        return testAttemptRepository.save(testAttempt);
    }

    @Override
    public List<TestAttempt> getAllAttemptsByUserId(Long userId) {
        // Implement logic to fetch attempts by user ID
        return testAttemptRepository.findAll();
    }

    @Override
    public TestAttempt getAttemptById(Long id) {
        return testAttemptRepository.findById(id).orElseThrow(() -> new RuntimeException("Attempt not found"));
    }
}
