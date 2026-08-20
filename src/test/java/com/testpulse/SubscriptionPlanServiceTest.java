package com.testpulse;

import com.testpulse.model.SubscriptionPlan;
import com.testpulse.repository.SubscriptionPlanRepository;
import com.testpulse.service.SubscriptionPlanService;
import com.testpulse.service.impl.SubscriptionPlanServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubscriptionPlanServiceTest {

    @Test
    void shouldReturnPlansFromRepository() {
        SubscriptionPlanRepository repository = mock(SubscriptionPlanRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                SubscriptionPlan.builder()
                        .id("plan_annual")
                        .planCode("ANNUAL_SUPER_PASS")
                        .title("Annual Super Pass")
                        .durationDays(365)
                        .originalAmountInPaise(99900L)
                        .discountedAmountInPaise(49900L)
                        .displayPrice("₹499")
                        .displayOriginalPrice("₹999")
                        .discountPercentage(50)
                        .badge("BEST VALUE")
                        .isRecommended(true)
                        .features(List.of("Unlimited Full-Length Mock Tests", "AI Weak Area Deep Analytics"))
                        .build()
        ));

        SubscriptionPlanService service = new SubscriptionPlanServiceImpl(repository);
        List<SubscriptionPlan> plans = service.getAllPlans();

        assertFalse(plans.isEmpty());
        assertEquals("plan_annual", plans.get(0).getId());
        assertEquals("ANNUAL_SUPER_PASS", plans.get(0).getPlanCode());
        assertEquals("₹499", plans.get(0).getDisplayPrice());
    }
}
