package com.testpulse.service.impl;

import com.testpulse.model.Question;
import com.testpulse.repository.QuestionRepository;
import com.testpulse.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<Question> getQuestionsByTestId(Long testId) {
        // Implement logic to fetch questions by test ID
        return questionRepository.findAll();
    }

    @Override
    public List<Question> createQuestions(List<Question> questions) {
        return questionRepository.saveAll(questions);
    }
}
