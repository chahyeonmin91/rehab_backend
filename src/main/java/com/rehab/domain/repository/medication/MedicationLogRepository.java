package com.rehab.domain.repository.medication;

import java.time.LocalDateTime;
import java.util.List;

import com.rehab.domain.entity.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {

	List<MedicationLog> findByUser_UserIdAndTakenAtBetween(
		Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<MedicationLog> findByUser_UserIdOrderByTakenAtDesc(Long userId);
}
