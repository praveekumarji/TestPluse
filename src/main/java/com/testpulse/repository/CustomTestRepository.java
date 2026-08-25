package com.testpulse.repository;

import com.testpulse.model.CustomTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomTestRepository extends JpaRepository<CustomTest, Long> {
    int countByOwnerUserIdAndActiveTrue(Long ownerUserId);

    List<CustomTest> findByOwnerUserIdAndActiveTrue(Long ownerUserId);

    @EntityGraph(attributePaths = {"questions", "questions.question"})
    Optional<CustomTest> findByIdAndOwnerUserIdAndActiveTrue(Long id, Long ownerUserId);
}
