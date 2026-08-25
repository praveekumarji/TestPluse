package com.testpulse.service.impl;

import com.testpulse.dto.CreateCustomTestRequest;
import com.testpulse.dto.CustomQuestionResponse;
import com.testpulse.dto.CustomTestPreviewResponse;
import com.testpulse.dto.CustomTestResponse;
import com.testpulse.dto.QuestionResponse;
import com.testpulse.model.CustomTest;
import com.testpulse.model.CustomTestQuestion;
import com.testpulse.model.Question;
import com.testpulse.model.TestType;
import com.testpulse.repository.CustomTestRepository;
import com.testpulse.repository.QuestionRepository;
import com.testpulse.service.CustomTestService;
import com.testpulse.util.LocalizedTextResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomTestServiceImpl implements CustomTestService {

    private static final int MAX_CUSTOM_TESTS_PER_USER = 5;

    private final CustomTestRepository customTestRepository;
    private final QuestionRepository questionRepository;

    public CustomTestServiceImpl(CustomTestRepository customTestRepository, QuestionRepository questionRepository) {
        this.customTestRepository = customTestRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomTestPreviewResponse generateCustomTest(String subject, int questionCount,
                                                         String difficultyLevel, String modeName, String lang) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject is required.");
        }
        if (questionCount < 1 || questionCount > 100) {
            throw new IllegalArgumentException("Question count must be between 1 and 100.");
        }

        String normalizedDifficulty = difficultyLevel == null || difficultyLevel.isBlank()
                ? "MEDIUM" : difficultyLevel.trim().toUpperCase(Locale.ROOT);
        String normalizedMode = modeName == null || modeName.isBlank()
                ? "PRACTICE" : modeName.trim().toUpperCase(Locale.ROOT);
        com.testpulse.model.difficulty difficulty = com.testpulse.model.difficulty.valueOf(normalizedDifficulty);
        com.testpulse.model.Modes mode = com.testpulse.model.Modes.valueOf(normalizedMode);

        List<Question> questions = questionRepository.findActiveForCustomTest(subject.trim(), mode, difficulty);
        Collections.shuffle(questions);
        if (questions.size() < questionCount) {
            throw new IllegalArgumentException("Only " + questions.size()
                    + " questions are available for the selected subject, mode, and difficulty.");
        }

        List<CustomQuestionResponse> selectedQuestions = questions.stream()
                .limit(questionCount)
                .map(question -> toPreviewQuestion(question, lang))
                .collect(Collectors.toList());

        return CustomTestPreviewResponse.builder()
                .id("custom_test_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                .title(subject.trim() + " " + normalizedMode + " Challenge")
                .subject(subject.trim())
                .description("Custom generated test with " + questionCount + " questions.")
                .durationMinutes(questionCount * 2)
                .totalQuestions(questionCount)
                .mode(mode.name())
                .testType(TestType.FREE.name())
                .questions(selectedQuestions)
                .build();
    }

    @Override
    public List<CustomTestResponse> getMyCustomTests(String lang) {
        Long userId = getCurrentUserId().orElseThrow(() -> new IllegalStateException("Authentication required."));
        return customTestRepository.findByOwnerUserIdAndActiveTrue(userId).stream()
                .map(customTest -> toResponse(customTest, lang))
                .collect(Collectors.toList());
    }

    @Override
    public CustomTestResponse getCustomTestById(Long id, String lang) {
        Long userId = getCurrentUserId().orElseThrow(() -> new IllegalStateException("Authentication required."));
        CustomTest customTest = customTestRepository.findByIdAndOwnerUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new RuntimeException("Custom test not found."));
        return toResponse(customTest, lang);
    }

    @Override
    @Transactional
    public CustomTestResponse createCustomTest(CreateCustomTestRequest request, String lang) {
        Long userId = getCurrentUserId().orElseThrow(() -> new IllegalStateException("Authentication required."));
        if (customTestRepository.countByOwnerUserIdAndActiveTrue(userId) >= MAX_CUSTOM_TESTS_PER_USER) {
            throw new IllegalArgumentException("You can only create up to " + MAX_CUSTOM_TESTS_PER_USER + " custom tests.");
        }

        if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
            throw new IllegalArgumentException("At least one question ID is required.");
        }
        if (request.getQuestionIds().size() > 50) {
            throw new IllegalArgumentException("A custom test can include up to 50 questions.");
        }

        CustomTest customTest = CustomTest.builder()
                .ownerUserId(userId)
                .title("Custom " + request.getSubject() + " Test")
                .titleHi("कस्टम " + request.getSubject() + " परीक्षण")
                .subject(request.getSubject())
                .subjectHi(request.getSubject())
                .description("Custom generated test for " + request.getSubject() + " with " + request.getQuestionIds().size() + " questions.")
                .descriptionHi(request.getSubject() + " के लिए " + request.getQuestionIds().size() + " प्रश्नों वाला कस्टम टेस्ट।")
                .durationMinutes("30")
                .mode(org.springframework.util.StringUtils.hasText(request.getMode()) ? com.testpulse.model.Modes.valueOf(request.getMode().trim().toUpperCase(Locale.ROOT)) : null)
                .difficulty(org.springframework.util.StringUtils.hasText(request.getDifficulty()) ? com.testpulse.model.difficulty.valueOf(request.getDifficulty().trim().toUpperCase(Locale.ROOT)) : null)
                .testType(TestType.PAID)
                .build();

        int position = 0;
        for (Long questionId : request.getQuestionIds()) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
            CustomTestQuestion customTestQuestion = CustomTestQuestion.builder()
                    .question(question)
                    .position(position++)
                    .build();
            customTest.addQuestion(customTestQuestion);
        }

        return toResponse(customTestRepository.save(customTest), lang);
    }

    @Override
    @Transactional
    public void deleteCustomTest(Long id) {
        Long userId = getCurrentUserId().orElseThrow(() -> new IllegalStateException("Authentication required."));
        CustomTest customTest = customTestRepository.findByIdAndOwnerUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new RuntimeException("Custom test not found."));
        customTest.setActive(false);
        customTestRepository.save(customTest);
    }

    @Override
    public List<QuestionResponse> getQuestionsForCustomTest(Long id, String lang) {
        Long userId = getCurrentUserId().orElseThrow(() -> new IllegalStateException("Authentication required."));
        CustomTest customTest = customTestRepository.findByIdAndOwnerUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new RuntimeException("Custom test not found."));

        return customTest.getQuestions().stream()
                .map(CustomTestQuestion::getQuestion)
                .filter(Question::isActive)
                .map(question -> applyLanguage(question, lang))
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
    }

    private Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.valueOf(authentication.getName()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private CustomTestResponse toResponse(CustomTest customTest, String lang) {
        return CustomTestResponse.builder()
                .id(customTest.getId())
                .title(LocalizedTextResolver.resolve(customTest.getTitle(), customTest.getTitleHi(), lang))
                .subject(LocalizedTextResolver.resolve(customTest.getSubject(), customTest.getSubjectHi(), lang))
                .description(LocalizedTextResolver.resolve(customTest.getDescription(), customTest.getDescriptionHi(), lang))
                .durationMinutes(customTest.getDurationMinutes())
                .mode(customTest.getMode() == null ? null : customTest.getMode().name())
                .difficulty(customTest.getDifficulty() == null ? null : customTest.getDifficulty().name())
                .testType(customTest.getTestType() == null ? null : customTest.getTestType().name())
                .active(customTest.isActive())
                .questionIds(customTest.getQuestions().stream()
                        .sorted((a, b) -> Integer.compare(a.getPosition() == null ? 0 : a.getPosition(), b.getPosition() == null ? 0 : b.getPosition()))
                        .map(customTestQuestion -> customTestQuestion.getQuestion().getId())
                        .collect(Collectors.toList()))
                .build();
    }

    private QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .testId(question.getTest() == null ? null : question.getTest().getId())
                .subject(question.getSubject())
                .text(question.getText())
                .options(question.getOptions())
                .correctOptionIndex(question.getCorrectOptionIndex())
                .explanation(question.getExplanation())
                .hint(question.getHint())
                .hintHi(question.getHintHi())
                .build();
    }

    private CustomQuestionResponse toPreviewQuestion(Question question, String lang) {
        boolean hindi = "hi".equalsIgnoreCase(lang) || "hindi".equalsIgnoreCase(lang);
        return CustomQuestionResponse.builder()
                .id(question.getId())
                .testId(question.getTest() == null ? null : question.getTest().getId())
                .subject(question.getSubject())
                .subjectHi(question.getSubjectHi())
                .topic(question.getTopic())
                .text(hindi && question.getTextHi() != null ? question.getTextHi() : question.getText())
                .textHi(question.getTextHi())
                .options(hindi && question.getOptionsHi() != null ? question.getOptionsHi() : question.getOptions())
                .optionsHi(question.getOptionsHi())
                .correctOptionIndex(question.getCorrectOptionIndex())
                .explanation(hindi && question.getExplanationHi() != null ? question.getExplanationHi() : question.getExplanation())
                .explanationHi(question.getExplanationHi())
                .hint(hindi && question.getHintHi() != null ? question.getHintHi() : question.getHint())
                .hintHi(question.getHintHi())
                .build();
    }

    private Question applyLanguage(Question question, String lang) {
        question.setText(LocalizedTextResolver.resolve(question.getText(), question.getTextHi(), lang));
        question.setSubject(LocalizedTextResolver.resolve(question.getSubject(), question.getSubjectHi(), lang));
        question.setExplanation(LocalizedTextResolver.resolve(question.getExplanation(), question.getExplanationHi(), lang));
        question.setHint(LocalizedTextResolver.resolve(question.getHint(), question.getHintHi(), lang));
        question.setOptions(LocalizedTextResolver.resolveList(question.getOptions(), question.getOptionsHi(), lang));
        return question;
    }
}
