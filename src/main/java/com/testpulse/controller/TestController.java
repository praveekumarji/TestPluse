package com.testpulse.controller;

import com.testpulse.dto.CreateCustomTestRequest;
import com.testpulse.dto.CustomTestPreviewResponse;
import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.dto.CreateTestRequest;
import com.testpulse.dto.QuestionResponse;
import com.testpulse.dto.TestResponse;
import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.service.CustomTestService;
import com.testpulse.service.QuestionService;
import com.testpulse.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    private final QuestionService questionService;

    private final CustomTestService customTestService;

    public TestController(TestService testService, QuestionService questionService, CustomTestService customTestService) {
        this.testService = testService;
        this.questionService = questionService;
        this.customTestService = customTestService;
    }

    @GetMapping
    public ResponseEntity<List<TestResponse>> getAllTests(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "en") String lang) {
        List<Test> allTests = testService.getAllTests(searchQuery, subject, classId, lang);
        List<TestResponse> response = allTests.stream().map(this::toTestResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long id,
                                                   @RequestParam(required = false) Long classId,
                                                   @RequestParam(defaultValue = "en") String lang) {
        Test test = testService.getTestById(id, classId, lang);
        return ResponseEntity.ok(toTestResponse(test));
    }

    @PostMapping("/custom")
        public ResponseEntity<?> createCustomTest(@RequestParam String subject,
                                           @RequestParam(defaultValue = "10") int questionCount,
                                           @RequestParam(defaultValue = "MEDIUM") String difficulty,
                                           @RequestParam(defaultValue = "PRACTICE") String mode,
                                           @RequestParam(defaultValue = "en") String lang,
                                           @RequestParam Long classId) {
            try {
                return ResponseEntity.ok(customTestService.generateCustomTest(
                        subject, questionCount, difficulty, mode, lang, classId));
            } catch (IllegalStateException ex) {
                return ResponseEntity.status(401).body(java.util.Map.of("error", ex.getMessage()));
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
            }
    }

    @GetMapping("/{testId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByTestId(@PathVariable Long testId,
                                                                      @RequestParam(required = false) Long classId,
                                                                      @RequestParam(defaultValue = "en") String lang) {
        testService.getTestById(testId, classId, lang);
        List<Question> questions = questionService.getQuestionsByTestId(testId, lang);
        List<QuestionResponse> response = questions.stream().map(this::toQuestionResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("TestController is working!");
    }

    public ResponseEntity<String> deleteTest(@PathVariable Long id) {
      //  testService.deleteTest(id);
        return ResponseEntity.ok("Test deleted successfully.");
    }

    private TestResponse toTestResponse(Test test) {
        if (test == null) {
            return null;
        }

        return TestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .subject(test.getSubject())
                .description(test.getDescription())
                .durationMinutes(test.getDurationMinutes())
                .mode(test.getMode() == null ? null : test.getMode().name())
                .difficulty(test.getDifficulty() == null ? null : test.getDifficulty().name())
                .testType(test.getTestType() == null ? null : test.getTestType().name())
                .classId(test.getEducationClass() == null ? null : test.getEducationClass().getId())
                .className(test.getEducationClass() == null ? null : test.getEducationClass().getName())
                .build();
    }

    private QuestionResponse toQuestionResponse(Question question) {
        if (question == null) {
            return null;
        }

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
}
