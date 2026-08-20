package com.testpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "custom_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerUserId;

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

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "customTest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CustomTestQuestion> questions = new LinkedHashSet<>();

    public void addQuestion(CustomTestQuestion question) {
        questions.add(question);
        question.setCustomTest(this);
    }
}
