package com.testpulse.service.impl;

import com.testpulse.dto.CouponApplyResponse;
import com.testpulse.dto.CouponRequest;
import com.testpulse.dto.CouponValidationRequest;
import com.testpulse.model.Coupon;
import com.testpulse.model.CouponUsage;
import com.testpulse.model.DiscountType;
import com.testpulse.repository.CouponRepository;
import com.testpulse.repository.CouponUsageRepository;
import com.testpulse.repository.SubscriptionPlanRepository;
import com.testpulse.service.CouponService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public CouponServiceImpl(CouponRepository couponRepository,
                            CouponUsageRepository couponUsageRepository,
                            SubscriptionPlanRepository subscriptionPlanRepository) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    @Cacheable(value = "coupons", key = "'all'")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @CacheEvict(value = "coupons", allEntries = true)
    @Transactional
    public Coupon createCoupon(CouponRequest request) {
        validateRequest(request);
        Coupon coupon = Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .title(request.getTitle())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minimumAmount(request.getMinimumAmount())
                .validFrom(request.getValidFrom() == null ? LocalDateTime.now() : request.getValidFrom())
                .validTo(request.getValidTo())
                .active(request.isActive())
                .maxUses(request.getMaxUses())
                .applicablePlanId(request.getApplicablePlanId())
                .createdAt(LocalDateTime.now())
                .build();

        return couponRepository.save(coupon);
    }

    @Override
    @CacheEvict(value = "coupons", allEntries = true)
    @Transactional
    public Coupon updateCoupon(String code, CouponRequest request) {
        Coupon existing = couponRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found."));

        validateRequest(request);
        existing.setTitle(request.getTitle());
        existing.setDiscountType(request.getDiscountType());
        existing.setDiscountValue(request.getDiscountValue());
        existing.setMinimumAmount(request.getMinimumAmount());
        existing.setValidFrom(request.getValidFrom() == null ? existing.getValidFrom() : request.getValidFrom());
        existing.setValidTo(request.getValidTo());
        existing.setActive(request.isActive());
        existing.setMaxUses(request.getMaxUses());
        existing.setApplicablePlanId(request.getApplicablePlanId());

        return couponRepository.save(existing);
    }

    @Override
    @CacheEvict(value = "coupons", allEntries = true)
    @Transactional
    public Coupon deactivateCoupon(String code) {
        Coupon coupon = couponRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found."));
        coupon.setActive(false);
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public CouponApplyResponse validateAndApplyCoupon(CouponValidationRequest request) {
        if (request == null) {
            return CouponApplyResponse.builder()
                    .success(false)
                    .valid(false)
                    .message("Invalid request payload")
                    .build();
        }

        return validateAndApplyCoupon(
                request.getCouponCode(),
                request.getPlanId(),
                request.getUserId(),
                request.getCurrentAmountInPaise()
        );
    }

    @Override
    @Transactional
    public CouponApplyResponse validateAndApplyCoupon(String couponCode, String planId, String userId, long currentAmountInPaise) {
        String normalizedCode = couponCode == null ? null : couponCode.trim().toUpperCase();
        if (normalizedCode == null || normalizedCode.isBlank()) {
            return buildFailure("Invalid or expired coupon code");
        }

        Coupon coupon = couponRepository.findById(normalizedCode)
                .orElseGet(() -> couponRepository.findByCode(normalizedCode).orElse(null));
        if (coupon == null || !coupon.isActive()) {
            return buildFailure("Invalid or expired coupon code");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return buildFailure("Invalid or expired coupon code");
        }
        if (coupon.getValidTo() != null && now.isAfter(coupon.getValidTo())) {
            return buildFailure("Invalid or expired coupon code");
        }

        if (coupon.getMaxUses() != null && couponUsageRepository.countByCouponCode(normalizedCode) >= coupon.getMaxUses()) {
            return buildFailure("Invalid or expired coupon code");
        }

        if (userId != null && !userId.isBlank()) {
            boolean alreadyUsed = couponUsageRepository.findByCouponCodeAndUserId(normalizedCode, userId).isPresent();
            if (alreadyUsed) {
             //   return buildFailure("Coupon has already been used by this user"); //commentd by praveen
            }
        }

        if (currentAmountInPaise < coupon.getMinimumAmount()) {
            return buildFailure("Coupon minimum cart amount not met");
        }

        if (coupon.getApplicablePlanId() != null && !coupon.getApplicablePlanId().isBlank() &&
                !coupon.getApplicablePlanId().equalsIgnoreCase(planId)) {
            return buildFailure("Coupon is not valid for this plan");
        }

        long discountAmountInPaise = calculateDiscountAmount(coupon, currentAmountInPaise);
        long finalAmountInPaise = Math.max(0, currentAmountInPaise - discountAmountInPaise);

        if (userId != null && !userId.isBlank()) {
            couponUsageRepository.save(CouponUsage.builder()
                    .couponCode(normalizedCode)
                    .userId(userId)
                    .usedAt(LocalDateTime.now())
                    .planId(planId)
                    .build());
        }

        String message = String.format("Coupon '%s' applied! You saved ₹%s", coupon.getCode(), formatRupees(discountAmountInPaise));
        return CouponApplyResponse.builder()
                .success(true)
                .valid(true)
                .message(message)
                .couponDetails(CouponApplyResponse.CouponDetails.builder()
                        .code(coupon.getCode())
                        .discountType(coupon.getDiscountType().name())
                        .discountValue((int) coupon.getDiscountValue())
                        .originalAmountInPaise(currentAmountInPaise)
                        .discountAmountInPaise(discountAmountInPaise)
                        .finalAmountInPaise(finalAmountInPaise)
                        .displayFinalPrice(formatRupeeDisplay(finalAmountInPaise))
                        .build())
                .build();
    }

    private CouponApplyResponse buildFailure(String message) {
        return CouponApplyResponse.builder()
                .success(false)
                .valid(false)
                .message(message)
                .build();
    }

    private long calculateDiscountAmount(Coupon coupon, long planAmount) {
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return Math.round((coupon.getDiscountValue() / 100.0) * planAmount);
        }
        return Math.min(coupon.getDiscountValue(), planAmount);
    }

    private String formatRupees(long amountInPaise) {
        return String.valueOf(amountInPaise / 100);
    }

    private String formatRupeeDisplay(long amountInPaise) {
        return "₹" + (amountInPaise / 100);
    }

    private void validateRequest(CouponRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Coupon payload is required.");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Coupon title is required.");
        }
        if (request.getDiscountType() == null) {
            throw new IllegalArgumentException("Discount type is required.");
        }
        if (request.getDiscountValue() < 0) {
            throw new IllegalArgumentException("Discount value cannot be negative.");
        }
        if (request.getMinimumAmount() < 0) {
            throw new IllegalArgumentException("Minimum amount cannot be negative.");
        }
        if (request.getDiscountType() == DiscountType.PERCENTAGE && request.getDiscountValue() > 100) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100.");
        }
        if (request.getApplicablePlanId() != null && !request.getApplicablePlanId().isBlank()) {
            subscriptionPlanRepository.findById(request.getApplicablePlanId())
                    .orElseThrow(() -> new IllegalArgumentException("Applicable plan does not exist."));
        }
    }
}
