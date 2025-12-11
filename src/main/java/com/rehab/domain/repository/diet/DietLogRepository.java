package com.rehab.domain.repository.diet;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehab.domain.entity.DietLog;

public interface DietLogRepository extends JpaRepository<DietLog, Integer> {

	List<DietLog> findByUser_UserIdAndLoggedAtBetween(
		Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<DietLog> findByUser_UserIdOrderByLoggedAtDesc(Long userId);
}
