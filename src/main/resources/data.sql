INSERT INTO user (email, password_hash, full_name, created_at) VALUES
('user1@example.com', 'hashed_password1', 'User One', NOW()),
('user2@example.com', 'hashed_password2', 'User Two', NOW());
package com.testpulse.service;
INSERT INTO test (title, subject, description, duration_minutes, mode, difficulty) VALUES
('Math Practice Test', 'Mathematics', 'Basic math practice', 30, 'PRACTICE', 'EASY'),
('Physics Exam', 'Physics', 'Physics exam for advanced learners', 60, 'EXAM', 'HARD');

INSERT INTO question (test_id, subject, topic, text, options, correct_option_index, explanation, hint) VALUES
(1, 'Mathematics', 'Algebra', 'What is 2+2?', '["2", "3", "4", "5"]', 2, 'The answer is 4.', 'Think of basic addition.'),
(2, 'Physics', 'Mechanics', 'What is the acceleration due to gravity?', '["9.8 m/s^2", "10 m/s^2", "8 m/s^2", "9 m/s^2"]', 0, 'The standard value is 9.8 m/s^2.', 'It is a constant value.');

import com.testpulse.model.Test;
import java.util.List;

public interface TestService {
    List<Test> getAllTests(String searchQuery, String subject);
    Test getTestById(Long id);
    Test createCustomTest(String subject, int questionCount, String difficulty, String mode);
}
