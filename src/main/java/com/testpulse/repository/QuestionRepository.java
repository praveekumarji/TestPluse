package com.testpulse.repository;

import com.testpulse.model.Question;
import com.testpulse.model.Modes;
import com.testpulse.model.difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTest_Id(Long testId);

    @Query("select q from Question q join fetch q.test t "
            + "where q.active = true and t.active = true "
            + "and (lower(t.subject) = lower(:subject) or lower(q.subject) = lower(:subject)) "
            + "and t.mode = :mode and t.difficulty = :difficulty")
    List<Question> findActiveForCustomTest(@Param("subject") String subject,
                                           @Param("mode") Modes mode,
                                           @Param("difficulty") difficulty difficulty);
}
