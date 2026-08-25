package com.testpulse.repository;

import com.testpulse.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(String email);
}