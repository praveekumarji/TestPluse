package com.testpulse;

import com.testpulse.dto.CouponApplyResponse;
import com.testpulse.model.Coupon;
import com.testpulse.model.DiscountType;
import com.testpulse.repository.CouponRepository;
import com.testpulse.repository.CouponUsageRepository;
import com.testpulse.repository.SubscriptionPlanRepository;
import com.testpulse.service.CouponService;
import com.testpulse.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CouponServiceTest {

    @Test
    void shouldApplyPercentageCouponAndReturnFinalPrice() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponUsageRepository couponUsageRepository = mock(CouponUsageRepository.class);
        SubscriptionPlanRepository subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);

        Coupon coupon = Coupon.builder()
                .code("SAVE10")
                .title("Save 10%")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .minimumAmount(0)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(10))
                .active(true)
                .maxUses(10)
                .usedCount(0)
                .applicablePlanId("plan_annual")
                .build();

        when(couponRepository.findById("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponUsageRepository.countByCouponCode("SAVE10")).thenReturn(0L);
        when(couponUsageRepository.findByCouponCodeAndUserId("SAVE10", "user_1")).thenReturn(Optional.empty());
        when(subscriptionPlanRepository.findById("plan_annual")).thenReturn(Optional.empty());

        CouponService couponService = new CouponServiceImpl(couponRepository, couponUsageRepository, subscriptionPlanRepository);
        CouponApplyResponse response = couponService.validateAndApplyCoupon("SAVE10", "plan_annual", "user_1", 1000L);

        assertTrue(response.isValid());
        assertEquals(100L, response.getCouponDetails().getDiscountAmountInPaise());
        assertEquals(900L, response.getCouponDetails().getFinalAmountInPaise());
    }
}
