package com.testpulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long testId;

    private double scorePercentage;

    private int correctCount;

    private int wrongCount;

    private int unattemptedCount;

    private int timeTakenSeconds;

    @Lob
    private String userAnswersJson;

    @Lob
    private String questionDetailsJson;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
