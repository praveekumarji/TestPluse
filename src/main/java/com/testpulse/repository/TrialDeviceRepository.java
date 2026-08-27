package com.testpulse.repository;

import com.testpulse.model.TrialDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrialDeviceRepository extends JpaRepository<TrialDevice, String> {
}