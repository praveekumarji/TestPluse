package com.testpulse.service;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    default List<Question> getQuestionsByTestId(Long testId) {
        return getQuestionsByTestId(testId, "en");
    }

    List<Question> getQuestionsByTestId(Long testId, String lang);

    List<Question> createQuestions(List<Question> questions);

    List<Question> createQuestionsFromDto(List<CreateQuestionRequest> requests);

    Question updateQuestion(Long id, Question question);

    void deactivateQuestion(Long id);

    List<Question> importQuestionsFromExcel(MultipartFile file);
}
