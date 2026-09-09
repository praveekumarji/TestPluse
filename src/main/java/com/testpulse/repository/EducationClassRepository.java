package com.testpulse.repository;

import com.testpulse.model.EducationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationClassRepository extends JpaRepository<EducationClass, Long> {
    List<EducationClass> findByActiveTrueOrderByNameAsc();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);
}