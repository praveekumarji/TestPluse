package com.testpulse.service;

import com.testpulse.dto.CouponApplyResponse;
import com.testpulse.dto.CouponRequest;
import com.testpulse.dto.CouponValidationRequest;
import com.testpulse.model.Coupon;

import java.util.List;

public interface CouponService {
    List<Coupon> getAllCoupons();

    Coupon createCoupon(CouponRequest request);

    Coupon updateCoupon(String code, CouponRequest request);

    Coupon deactivateCoupon(String code);

    CouponApplyResponse validateAndApplyCoupon(CouponValidationRequest request);

    CouponApplyResponse validateAndApplyCoupon(String couponCode, String planId, String userId, long currentAmountInPaise);
}
