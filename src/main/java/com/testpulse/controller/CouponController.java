package com.testpulse.controller;

import com.testpulse.dto.CouponApplyResponse;
import com.testpulse.dto.CouponRequest;
import com.testpulse.dto.CouponValidationRequest;
import com.testpulse.model.Coupon;
import com.testpulse.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> getCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest request) {
        try {
            return ResponseEntity.ok(couponService.createCoupon(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/coupons/{code}")
    public ResponseEntity<?> updateCoupon(@PathVariable String code,
                                         @RequestBody CouponRequest request) {
        try {
            return ResponseEntity.ok(couponService.updateCoupon(code, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PatchMapping("/coupons/{code}/deactivate")
    public ResponseEntity<?> deactivateCoupon(@PathVariable String code) {
        try {
            return ResponseEntity.ok(couponService.deactivateCoupon(code));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/coupons/validate")
    public ResponseEntity<CouponApplyResponse> validateCoupon(@RequestBody CouponValidationRequest request) {
        return ResponseEntity.ok(couponService.validateAndApplyCoupon(request));
    }

    @PostMapping("/coupons/calculate")
    public ResponseEntity<CouponApplyResponse> calculateFinalPrice(
            @RequestParam String couponCode,
            @RequestParam String planId,
            @RequestParam String userId,
            @RequestParam long currentAmountInPaise) {
        return ResponseEntity.ok(couponService.validateAndApplyCoupon(couponCode, planId, userId, currentAmountInPaise));
    }
}
