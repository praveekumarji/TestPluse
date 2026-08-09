package com.testpulse.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String testId;

    @Column(nullable = false)
    private String subject;

    private String topic;

    @Column(nullable = false)
    private String text;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> options;

    @Column(nullable = false)
    private int correctOptionIndex;

    private String explanation;

    private String hint;
}
