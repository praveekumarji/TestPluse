package com.testpulse.controller;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.dto.CreateTestRequest;
import com.testpulse.dto.UpdateQuestionRequest;
import com.testpulse.dto.UpdateTestRequest;
import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.service.QuestionService;
import com.testpulse.service.TestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TestService testService;
    private final QuestionService questionService;

    public AdminController(TestService testService, QuestionService questionService) {
        this.testService = testService;
        this.questionService = questionService;
    }

    @PostMapping("/addtests")
    public ResponseEntity<?> addTests(@Valid @RequestBody List<CreateTestRequest> requests) {
        try {
            List<Test> createdTests = testService.addTestsFromDto(requests);
            return ResponseEntity.ok(createdTests);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/addquestions")
    public ResponseEntity<?> addQuestions(@Valid @RequestBody List<CreateQuestionRequest> requests) {
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

    @PutMapping("/tests/{id}")
    public ResponseEntity<?> updateTest(@PathVariable Long id, @Valid @RequestBody UpdateTestRequest request) {
        try {
            Test test = Test.builder()
                    .title(request.getTitle())
                    .titleHi(request.getTitleHi())
                    .subject(request.getSubject())
                    .subjectHi(request.getSubjectHi())
                    .description(request.getDescription())
                    .descriptionHi(request.getDescriptionHi())
                    .durationMinutes(request.getDurationMinutes())
                    .mode(request.getMode() == null ? null : com.testpulse.model.Modes.valueOf(request.getMode().trim().toUpperCase()))
                    .difficulty(request.getDifficulty() == null ? null : com.testpulse.model.difficulty.valueOf(request.getDifficulty().trim().toUpperCase()))
                    .testType(request.getTestType() == null ? null : com.testpulse.model.TestType.valueOf(request.getTestType().trim().toUpperCase()))
                    .active(request.getActive() == null ? true : request.getActive())
                    .build();
            return ResponseEntity.ok(testService.updateTest(id, test));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/tests/{id}/deactivate")
    public ResponseEntity<?> deactivateTest(@PathVariable Long id) {
        try {
            testService.deactivateTest(id);
            return ResponseEntity.ok("Test deactivated successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody UpdateQuestionRequest request) {
        try {
            Question question = Question.builder()
                    .subject(request.getSubject())
                    .subjectHi(request.getSubjectHi())
                    .text(request.getText())
                    .textHi(request.getTextHi())
                    .options(request.getOptions())
                    .optionsHi(request.getOptionsHi())
                    .correctOptionIndex(request.getCorrectOptionIndex() == null ? -1 : request.getCorrectOptionIndex())
                    .explanation(request.getExplanation())
                    .explanationHi(request.getExplanationHi())
                    .hint(request.getHint())
                    .hintHi(request.getHintHi())
                    .active(request.getActive() == null ? true : request.getActive())
                    .build();
            return ResponseEntity.ok(questionService.updateQuestion(id, question));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/questions/{id}/deactivate")
    public ResponseEntity<?> deactivateQuestion(@PathVariable Long id) {
        try {
            questionService.deactivateQuestion(id);
            return ResponseEntity.ok("Question deactivated successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
