package com.testpulse.service;

import com.testpulse.model.TestAttempt;
import java.util.List;

public interface TestAttemptService {
    TestAttempt submitTestAttempt(TestAttempt testAttempt);
    List<TestAttempt> getAllAttemptsByUserId(Long userId);
    TestAttempt getAttemptById(Long id);
}
