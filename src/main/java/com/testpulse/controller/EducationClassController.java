package com.testpulse.controller;

import com.testpulse.dto.CreateEducationClassRequest;
import com.testpulse.model.EducationClass;
import com.testpulse.service.EducationClassService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class EducationClassController {

    private final EducationClassService educationClassService;

    public EducationClassController(EducationClassService educationClassService) {
        this.educationClassService = educationClassService;
    }

    @GetMapping
    public ResponseEntity<List<EducationClass>> getClasses() {
        return ResponseEntity.ok(educationClassService.getActiveClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationClass> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(educationClassService.getActiveClassById(id));
    }

    @PostMapping
    public ResponseEntity<?> createClass(@Valid @RequestBody CreateEducationClassRequest request) {
        try {
            return ResponseEntity.ok(educationClassService.createClass(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}