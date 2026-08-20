package com.testpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(nullable = false)
    private String subject;

    @Column
    private String subjectHi;

    private String topic;

    @Column(nullable = false)
    private String text;

    @Column
    private String textHi;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> options;

    @ElementCollection
    @CollectionTable(name = "question_options_hi", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> optionsHi;

    @Column(nullable = false)
    private int correctOptionIndex;

    @Column
    private String explanation;

    @Column
    private String explanationHi;

    @Column
    private String hint;

    @Column
    private String hintHi;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Transient
    public String getTestId() {
        return test == null ? null : String.valueOf(test.getId());
    }

    @Transient
    public void setTestId(String testId) {
        if (testId == null || testId.isBlank()) {
            this.test = null;
            return;
        }

        Test relatedTest = new Test();
        relatedTest.setId(Long.parseLong(testId));
        this.test = relatedTest;
    }

    @Transient
    public void setTestId(Long testId) {
        if (testId == null) {
            this.test = null;
            return;
        }

        Test relatedTest = new Test();
        relatedTest.setId(testId);
        this.test = relatedTest;
    }
}
