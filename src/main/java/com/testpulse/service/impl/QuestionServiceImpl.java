package com.testpulse.service.impl;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.repository.QuestionRepository;
import com.testpulse.repository.TestRepository;
import com.testpulse.service.QuestionService;
import com.testpulse.util.LocalizedTextResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public List<Question> getQuestionsByTestId(Long testId, String lang) {
        List<Question> questions = questionRepository.findByTest_Id(testId);
        return questions.stream().map(question -> applyLanguage(question, lang)).toList();
    }

    @Override
    public List<Question> createQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("At least one question is required.");
        }

        for (Question question : questions) {
            validateQuestion(question);
            resolveTestReference(question);
        }

        return questionRepository.saveAll(questions);
    }

    @Override
    public List<Question> createQuestionsFromDto(List<CreateQuestionRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one question is required.");
        }

        List<Question> questions = new java.util.ArrayList<>();
        for (CreateQuestionRequest request : requests) {
            questions.add(mapToEntity(request));
        }
        return createQuestions(questions);
    }

    private Question mapToEntity(CreateQuestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Question payload cannot be null.");
        }
        if (request.getTestId() == null) {
            throw new IllegalArgumentException("Question testId is required.");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("Question subject is required.");
        }
        if (request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("Question text is required.");
        }
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new IllegalArgumentException("Each question must include at least two options.");
        }
        if (request.getCorrectOptionIndex() < 0 || request.getCorrectOptionIndex() >= request.getOptions().size()) {
            throw new IllegalArgumentException("Correct option index is out of range.");
        }
        if (request.getOptionsHi() != null && !request.getOptionsHi().isEmpty() && request.getOptionsHi().size() != request.getOptions().size()) {
            throw new IllegalArgumentException("Hindi options count must match English options count.");
        }

        Question question = Question.builder()
                .subject(request.getSubject())
                .subjectHi(request.getSubjectHi())
                .text(request.getText())
                .textHi(request.getTextHi())
                .options(request.getOptions())
                .optionsHi(request.getOptionsHi())
                .correctOptionIndex(request.getCorrectOptionIndex())
                .explanation(request.getExplanation())
                .explanationHi(request.getExplanationHi())
                .hint(request.getHint())
                .build();

        question.setTestId(request.getTestId());
        return question;
    }

    private void validateQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question payload cannot be null.");
        }
        if (question.getTest() == null && (question.getTestId() == null || question.getTestId().isBlank())) {
            throw new IllegalArgumentException("Question testId is required.");
        }
        if (question.getSubject() == null || question.getSubject().isBlank()) {
            throw new IllegalArgumentException("Question subject is required.");
        }
        if (question.getText() == null || question.getText().isBlank()) {
            throw new IllegalArgumentException("Question text is required.");
        }
        if (question.getOptions() == null || question.getOptions().size() < 2) {
            throw new IllegalArgumentException("Each question must include at least two options.");
        }
        if (question.getCorrectOptionIndex() < 0 || question.getCorrectOptionIndex() >= question.getOptions().size()) {
            throw new IllegalArgumentException("Correct option index is out of range.");
        }
        if (question.getOptionsHi() != null && !question.getOptionsHi().isEmpty() && question.getOptionsHi().size() != question.getOptions().size()) {
            throw new IllegalArgumentException("Hindi options count must match English options count.");
        }
    }

    private void resolveTestReference(Question question) {
        if (question.getTest() != null) {
            return;
        }

        String testId = question.getTestId();
        if (testId == null || testId.isBlank()) {
            throw new IllegalArgumentException("Question testId is required.");
        }

        Test test = testRepository.findById(Long.valueOf(testId))
                .orElseThrow(() -> new IllegalArgumentException("Test not found for id: " + testId));
        question.setTest(test);
    }

    private Question applyLanguage(Question question, String lang) {
        if (question == null) {
            return null;
        }

        question.setText(LocalizedTextResolver.resolve(question.getText(), question.getTextHi(), lang));
        question.setSubject(LocalizedTextResolver.resolve(question.getSubject(), question.getSubjectHi(), lang));
        question.setExplanation(LocalizedTextResolver.resolve(question.getExplanation(), question.getExplanationHi(), lang));
        question.setOptions(LocalizedTextResolver.resolveList(question.getOptions(), question.getOptionsHi(), lang));
        return question;
    }
}
