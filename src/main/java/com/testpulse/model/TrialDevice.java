package com.testpulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialDevice {
    @Id
    @Column(name = "device_hash", length = 128)
    private String deviceHash;

    @Column(name = "first_user_id", nullable = false)
    private Long firstUserId;

    @Column(name = "trial_used_at", nullable = false)
    private LocalDateTime trialUsedAt;
}