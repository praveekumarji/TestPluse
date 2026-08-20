package com.testpulse.controller;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.dto.CreateTestRequest;
import com.testpulse.dto.QuestionResponse;
import com.testpulse.dto.TestResponse;
import com.testpulse.model.Question;
import com.testpulse.model.Test;
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

    public TestController(TestService testService, QuestionService questionService) {
        this.testService = testService;
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<TestResponse>> getAllTests(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String subject,
            @RequestParam(defaultValue = "en") String lang) {
        List<Test> allTests = testService.getAllTests(searchQuery, subject, lang);
        List<TestResponse> response = allTests.stream().map(this::toTestResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "en") String lang) {
        Test test = testService.getTestById(id, lang);
        return ResponseEntity.ok(toTestResponse(test));
    }

    @PostMapping("/custom")
    public ResponseEntity<Test> createCustomTest(@RequestParam String subject,
                                               @RequestParam int questionCount,
                                               @RequestParam String difficulty,
                                               @RequestParam String mode,
                                               @RequestParam(defaultValue = "en") String lang) {
        Test test = testService.createCustomTest(subject, questionCount, difficulty, mode, lang);
        return ResponseEntity.ok(test);
    }

    @GetMapping("/{testId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByTestId(@PathVariable Long testId,
                                                                      @RequestParam(defaultValue = "en") String lang) {
        List<Question> questions = questionService.getQuestionsByTestId(testId, lang);
        List<QuestionResponse> response = questions.stream().map(this::toQuestionResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addtests")
    public ResponseEntity<?> addTests(@RequestBody List<CreateTestRequest> requests) {
        try {
            List<Test> createdTests = testService.addTestsFromDto(requests);
            return ResponseEntity.ok(createdTests);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/addquestions")
    public ResponseEntity<?> createquestion(@RequestBody List<CreateQuestionRequest> requests) {
        try {
            List<Question> createdQuestions = questionService.createQuestionsFromDto(requests);
            return ResponseEntity.ok(createdQuestions);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/upload-questions")
    public ResponseEntity<?> uploadQuestionsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Question> createdQuestions = questionService.importQuestionsFromExcel(file);
            return ResponseEntity.ok(createdQuestions);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/test")
   public ResponseEntity<String> test(){
        return ResponseEntity.ok("TestController is working!");
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
                .build();
    }
}
