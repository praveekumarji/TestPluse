package com.testpulse.repository;

import com.testpulse.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    Optional<CouponUsage> findByCouponCodeAndUserId(String couponCode, String userId);

    long countByCouponCode(String couponCode);
}
