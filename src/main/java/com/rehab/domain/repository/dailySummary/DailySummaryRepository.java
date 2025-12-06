package com.rehab.domain.repository.dailySummary;

import com.rehab.domain.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DailySummary Repository
 */
@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {

	/**
	 * 특정 날짜의 일일 요약 조회
	 */
	Optional<DailySummary> findByUser_UserIdAndDate(Long userId, LocalDateTime date);

	/**
	 * 특정 기간의 일일 요약 조회
	 */
	@Query("SELECT ds FROM DailySummary ds " +
		"WHERE ds.user.userId = :userId " +
		"AND ds.date BETWEEN :startDate AND :endDate " +
		"ORDER BY ds.date DESC")
	List<DailySummary> findByUserIdAndDateRange(
		@Param("userId") Long userId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 최근 N일간의 일일 요약 조회
	 */
	@Query("SELECT ds FROM DailySummary ds " +
		"WHERE ds.user.userId = :userId " +
		"ORDER BY ds.date DESC " +
		"LIMIT :days")
	List<DailySummary> findRecentDays(
		@Param("userId") Long userId,
		@Param("days") int days
	);

	@Query("SELECT ds FROM DailySummary ds " +
		"WHERE ds.user.userId = :userId " +
		"AND ds.date BETWEEN :startDate AND :endDate " +
		"ORDER BY ds.date ASC")
	List<DailySummary> findByUserIdAndDateBetween(
		@Param("userId") Long userId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
	);
}







