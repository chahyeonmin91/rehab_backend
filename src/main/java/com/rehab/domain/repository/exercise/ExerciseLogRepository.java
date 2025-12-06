package com.rehab.domain.repository.exercise;

import com.rehab.domain.entity.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ExerciseLog Repository
 */
@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

	/**
	 * 특정 날짜의 운동 로그 조회
	 */
	@Query("SELECT el FROM ExerciseLog el " +
		"WHERE el.user.userId = :userId " +
		"AND DATE(el.loggedAt) = :date " +
		"ORDER BY el.loggedAt DESC")
	List<ExerciseLog> findByUserIdAndDate(
		@Param("userId") Long userId,
		@Param("date") LocalDateTime date
	);

	// user_id 와 logged_at 컬럼 기준으로 하루 범위 조회
	List<ExerciseLog> findByUser_UserIdAndLoggedAtBetween(
		Long userId,
		LocalDateTime start,
		LocalDateTime end
	);

	/**
	 * 특정 기간의 운동 로그 조회
	 */
	@Query("SELECT el FROM ExerciseLog el " +
		"WHERE el.user.userId = :userId " +
		"AND el.loggedAt BETWEEN :startDate AND :endDate " +
		"ORDER BY el.loggedAt DESC")
	List<ExerciseLog> findByUserIdAndDateRange(
		@Param("userId") Long userId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 특정 플랜 항목의 로그가 특정 날짜에 존재하는지 확인
	 */
	@Query("SELECT el FROM ExerciseLog el " +
		"WHERE el.planItem.planItemId = :planItemId " +
		"AND DATE(el.loggedAt) = :date")
	Optional<ExerciseLog> findByPlanItemIdAndDate(
		@Param("planItemId") Long planItemId,
		@Param("date") LocalDate date
	);

	/**
	 * 특정 날짜의 완료된 운동 로그 개수
	 */
	@Query("SELECT COUNT(el) FROM ExerciseLog el " +
		"WHERE el.user.userId = :userId " +
		"AND DATE(el.loggedAt) = :date " +
		"AND el.completionRate >= 80")
	long countCompletedByUserIdAndDate(
		@Param("userId") Long userId,
		@Param("date") LocalDate date
	);
}







