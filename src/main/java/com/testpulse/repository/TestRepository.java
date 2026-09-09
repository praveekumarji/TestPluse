package com.testpulse.repository;

import com.testpulse.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findBySubjectContainingIgnoreCase(String subject);

    @Query("select t from Test t "
            + "left join fetch t.educationClass "
            + "where t.active = true "
            + "and (:classId is null or t.educationClass.id = :classId) "
            + "and (:subject is null or :subject = '' or lower(t.subject) like lower(concat('%', :subject, '%'))) ")
    List<Test> findActiveByClassAndSubject(@Param("classId") Long classId,
                                           @Param("subject") String subject);
}
