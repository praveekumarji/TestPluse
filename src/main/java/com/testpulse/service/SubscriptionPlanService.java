package com.testpulse.service;

import com.testpulse.model.SubscriptionPlan;

import java.util.List;

public interface SubscriptionPlanService {
    List<SubscriptionPlan> getAllPlans();

    SubscriptionPlan createPlan(SubscriptionPlan plan);

    SubscriptionPlan updatePlan(String id, SubscriptionPlan plan);

    void deletePlan(String id);
}
