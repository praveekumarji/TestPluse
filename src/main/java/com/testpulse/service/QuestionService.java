package com.testpulse.service;

import com.testpulse.model.Question;
import java.util.List;

public interface QuestionService {
    List<Question> getQuestionsByTestId(Long testId);
    List<Question> createQuestions(List<Question> questions);
}
