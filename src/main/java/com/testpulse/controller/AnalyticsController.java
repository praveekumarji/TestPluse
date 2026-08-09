package com.testpulse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @GetMapping("/summary")
    public ResponseEntity<String> getAnalyticsSummary() {
        // Implement analytics summary logic
        return ResponseEntity.ok("Analytics Summary");
    }
}
