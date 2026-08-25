package com.testpulse.service.impl;

import com.testpulse.model.SubscriptionPlan;
import com.testpulse.repository.SubscriptionPlanRepository;
import com.testpulse.service.SubscriptionPlanService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'all'")
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    @Override
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        validatePlan(plan);
        if (plan.getId() == null || plan.getId().isBlank()) {
            throw new IllegalArgumentException("Plan id is required.");
        }
        if (subscriptionPlanRepository.existsById(plan.getId())) {
            throw new IllegalArgumentException("Plan with this id already exists.");
        }
        return subscriptionPlanRepository.save(plan);
    }

    @Override
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public SubscriptionPlan updatePlan(String id, SubscriptionPlan plan) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Plan id is required.");
        }
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found."));

        validatePlan(plan);
        existing.setPlanCode(plan.getPlanCode());
        existing.setTitle(plan.getTitle());
        existing.setSubtitle(plan.getSubtitle());
        existing.setDurationDays(plan.getDurationDays());
        existing.setOriginalAmountInPaise(plan.getOriginalAmountInPaise());
        existing.setDiscountedAmountInPaise(plan.getDiscountedAmountInPaise());
        existing.setDisplayPrice(plan.getDisplayPrice());
        existing.setDisplayOriginalPrice(plan.getDisplayOriginalPrice());
        existing.setDiscountPercentage(plan.getDiscountPercentage());
        existing.setBadge(plan.getBadge());
        existing.setRecommended(plan.isRecommended());
        existing.setFeatures(plan.getFeatures());

        return subscriptionPlanRepository.save(existing);
    }

    @Override
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public void deletePlan(String id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("Plan not found.");
        }
        subscriptionPlanRepository.deleteById(id);
    }

    private void validatePlan(SubscriptionPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan payload is required.");
        }
        if (plan.getPlanCode() == null || plan.getPlanCode().isBlank()) {
            throw new IllegalArgumentException("Plan code is required.");
        }
        if (plan.getTitle() == null || plan.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (plan.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Duration days must be greater than zero.");
        }
        if (plan.getDiscountedAmountInPaise() < 0 || plan.getOriginalAmountInPaise() < 0) {
            throw new IllegalArgumentException("Price values cannot be negative.");
        }
    }
}
