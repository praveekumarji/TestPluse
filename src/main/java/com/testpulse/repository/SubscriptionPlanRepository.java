package com.testpulse.repository;

import com.testpulse.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String> {
	@EntityGraph(attributePaths = "educationClasses")
	List<SubscriptionPlan> findAll();

	@EntityGraph(attributePaths = "educationClasses")
	List<SubscriptionPlan> findDistinctByEducationClasses_Id(Long classId);
}
