package com.testpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String titleHi;

    @Column(nullable = false)
    private String subject;

    @Column
    private String subjectHi;

    @Column(nullable = false)
    private String description;

    @Column
    private String descriptionHi;

    @Column
    private String durationMinutes;

    @Enumerated(EnumType.STRING)
    private Modes mode;

    @Enumerated(EnumType.STRING)
    private difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TestType testType = TestType.FREE;
}
