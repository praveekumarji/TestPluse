package com.testpulse.service;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.model.Question;

import java.util.List;

public interface QuestionService {
    default List<Question> getQuestionsByTestId(Long testId) {
        return getQuestionsByTestId(testId, "en");
    }

    List<Question> getQuestionsByTestId(Long testId, String lang);

    List<Question> createQuestions(List<Question> questions);

    List<Question> createQuestionsFromDto(List<CreateQuestionRequest> requests);
}
