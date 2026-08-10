package com.testpulse.service.impl;

import com.testpulse.dto.CreateTestRequest;
import com.testpulse.model.Modes;
import com.testpulse.model.Test;
import com.testpulse.model.difficulty;
import com.testpulse.repository.TestRepository;
import com.testpulse.service.TestService;
import com.testpulse.util.LocalizedTextResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public List<Test> getAllTests(String searchQuery, String subject, String lang) {
        List<Test> tests = (subject != null && !subject.isBlank())
                ? testRepository.findBySubjectContainingIgnoreCase(subject)
                : testRepository.findAll();

        if (searchQuery != null && !searchQuery.isBlank()) {
            String query = searchQuery.toLowerCase(Locale.ROOT);
            tests = tests.stream()
                    .filter(test ->
                            (test.getTitle() != null && test.getTitle().toLowerCase(Locale.ROOT).contains(query)) ||
                            (test.getDescription() != null && test.getDescription().toLowerCase(Locale.ROOT).contains(query)) ||
                            (test.getTitleHi() != null && test.getTitleHi().toLowerCase(Locale.ROOT).contains(query)))
                    .toList();
        }

        return tests.stream().map(test -> applyLanguage(test, lang)).toList();
    }

    @Override
    public Test getTestById(Long id, String lang) {
        Test test = testRepository.findById(id).orElseThrow(() -> new RuntimeException("Test not found"));
        return applyLanguage(test, lang);
    }

    @Override
    public Test createCustomTest(String subject, int questionCount, String difficultyLevel, String mode, String lang) {
        String normalizedSubject = subject == null ? "General" : subject.trim();
        String normalizedDifficulty = difficultyLevel == null || difficultyLevel.isBlank() ? "MEDIUM" : difficultyLevel.trim().toUpperCase(Locale.ROOT);
        String normalizedMode = mode == null || mode.isBlank() ? "PRACTICE" : mode.trim().toUpperCase(Locale.ROOT);

        Test test = Test.builder()
                .title("Custom " + normalizedSubject + " Test")
                .titleHi("कस्टम " + translateSubjectToHindi(normalizedSubject) + " परीक्षण")
                .subject(normalizedSubject)
                .subjectHi(translateSubjectToHindi(normalizedSubject))
                .description("Custom generated test for " + normalizedSubject + " with " + questionCount + " questions.")
                .descriptionHi(normalizedSubject + " के लिए " + questionCount + " प्रश्नों वाला कस्टम जनरेटेड टेस्ट।")
                .durationMinutes("30")
                .mode(Modes.valueOf(normalizedMode))
                .difficulty(difficulty.valueOf(normalizedDifficulty))
                .build();

        return applyLanguage(testRepository.save(test), lang);
    }

    @Override
    public List<Test> addTest(List<Test> tests) {
        if (tests == null || tests.isEmpty()) {
            throw new IllegalArgumentException("At least one test is required.");
        }

        List<Test> savedTests = new ArrayList<>();
        for (Test test : tests) {
            validateTest(test);
            savedTests.add(testRepository.save(test));
        }
        return savedTests;
    }

    @Override
    public List<Test> addTestsFromDto(List<CreateTestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one test is required.");
        }

        List<Test> tests = new ArrayList<>();
        for (CreateTestRequest request : requests) {
            tests.add(mapToEntity(request));
        }
        return addTest(tests);
    }

    private Test mapToEntity(CreateTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Test payload cannot be null.");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Test title is required.");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("Test subject is required.");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Test description is required.");
        }
        if (request.getMode() == null || request.getMode().isBlank()) {
            throw new IllegalArgumentException("Test mode is required.");
        }
        if (request.getDifficulty() == null || request.getDifficulty().isBlank()) {
            throw new IllegalArgumentException("Test difficulty is required.");
        }

        return Test.builder()
                .title(request.getTitle())
                .titleHi(request.getTitleHi())
                .subject(request.getSubject())
                .subjectHi(request.getSubjectHi())
                .description(request.getDescription())
                .descriptionHi(request.getDescriptionHi())
                .durationMinutes(request.getDurationMinutes())
                .mode(Modes.valueOf(request.getMode().trim().toUpperCase(Locale.ROOT)))
                .difficulty(difficulty.valueOf(request.getDifficulty().trim().toUpperCase(Locale.ROOT)))
                .build();
    }

    private void validateTest(Test test) {
        if (test == null) {
            throw new IllegalArgumentException("Test payload cannot be null.");
        }
        if (test.getTitle() == null || test.getTitle().isBlank()) {
            throw new IllegalArgumentException("Test title is required.");
        }
        if (test.getSubject() == null || test.getSubject().isBlank()) {
            throw new IllegalArgumentException("Test subject is required.");
        }
        if (test.getDescription() == null || test.getDescription().isBlank()) {
            throw new IllegalArgumentException("Test description is required.");
        }
        if (test.getMode() == null) {
            throw new IllegalArgumentException("Test mode is required.");
        }
        if (test.getDifficulty() == null) {
            throw new IllegalArgumentException("Test difficulty is required.");
        }
    }

    private Test applyLanguage(Test test, String lang) {
        if (test == null) {
            return null;
        }

        test.setTitle(LocalizedTextResolver.resolve(test.getTitle(), test.getTitleHi(), lang));
        test.setSubject(LocalizedTextResolver.resolve(test.getSubject(), test.getSubjectHi(), lang));
        test.setDescription(LocalizedTextResolver.resolve(test.getDescription(), test.getDescriptionHi(), lang));
        return test;
    }

    private String translateSubjectToHindi(String subject) {
        if (subject == null || subject.isBlank()) {
            return "सामान्य";
        }

        return switch (subject.toLowerCase(Locale.ROOT)) {
            case "java" -> "जावा";
            case "database" -> "डेटाबेस";
            case "math" -> "गणित";
            case "science" -> "विज्ञान";
            default -> subject;
        };
    }
}
