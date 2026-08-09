package com.testpulse.controller;

import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.service.QuestionService;
import com.testpulse.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Test>> getAllTests(@RequestParam(required = false) String searchQuery, @RequestParam(required = false) String subject) {
        List<Test> allTests = testService.getAllTests(searchQuery, subject);
        return ResponseEntity.ok(allTests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable Long id) {
        Test test = testService.getTestById(id);
        return ResponseEntity.ok(test);
    }

    @PostMapping("/custom")
    public ResponseEntity<Test> createCustomTest(@RequestParam String subject, @RequestParam int questionCount, @RequestParam String difficulty, @RequestParam String mode) {
        Test test = testService.createCustomTest(subject, questionCount, difficulty, mode);
        return ResponseEntity.ok(test);
    }

    @GetMapping("/{testId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByTestId(@PathVariable Long testId) {
        List<Question> questions = questionService.getQuestionsByTestId(testId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/addtests")
    public ResponseEntity addTests(@RequestBody List<Test> tests) {
        List<Test> createdTest= testService.addTest(tests);
        return ResponseEntity.ok(createdTest);
    }
@PostMapping("/addquestions")
public ResponseEntity<List<Question>> createquestion(@RequestBody List<Question> questions) {
            List<Question> createdQuestions = questionService.createQuestions(questions);
        return ResponseEntity.ok(createdQuestions);
    }

    @GetMapping("/test")
   public ResponseEntity<String> test(){
        return ResponseEntity.ok("TestController is working!");
   }
}
