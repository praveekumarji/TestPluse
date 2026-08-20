package com.testpulse.controller;

import com.testpulse.model.SubscriptionPlan;
import com.testpulse.service.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping("/subscription-plans")
    public ResponseEntity<List<SubscriptionPlan>> getSubscriptionPlans() {
        return ResponseEntity.ok(subscriptionPlanService.getAllPlans());
    }

    @PostMapping("/subscription-plans")
    public ResponseEntity<?> createSubscriptionPlan(@RequestBody SubscriptionPlan plan) {
        try {
            return ResponseEntity.ok(subscriptionPlanService.createPlan(plan));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/subscription-plans/{id}")
    public ResponseEntity<?> updateSubscriptionPlan(@PathVariable String id,
                                                   @RequestBody SubscriptionPlan plan) {
        try {
            return ResponseEntity.ok(subscriptionPlanService.updatePlan(id, plan));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/subscription-plans/{id}")
    public ResponseEntity<?> deleteSubscriptionPlan(@PathVariable String id) {
        try {
            subscriptionPlanService.deletePlan(id);
            return ResponseEntity.ok(Map.of("message", "Plan deleted successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
