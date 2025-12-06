package com.rehab.domain.repository.recovery;

import com.rehab.domain.entity.RecoveryScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecoveryScoreRepository extends JpaRepository<RecoveryScore, Long> {

	/**
	 * 특정 사용자의 특정 날짜 회복 점수 조회
	 */
	Optional<RecoveryScore> findByUser_UserIdAndDate(Long userId, LocalDate date);

	/**
	 * 특정 사용자의 가장 최근 회복 점수 조회
	 */
	@Query("SELECT rs FROM RecoveryScore rs " +
		"WHERE rs.user.userId = :userId " +
		"ORDER BY rs.date DESC " +
		"LIMIT 1")
	Optional<RecoveryScore> findLatestByUserId(@Param("userId") Long userId);

	/**
	 * 특정 사용자의 날짜 범위 내 회복 점수 목록 조회
	 */
	@Query("SELECT rs FROM RecoveryScore rs " +
		"WHERE rs.user.userId = :userId " +
		"AND rs.date BETWEEN :startDate AND :endDate " +
		"ORDER BY rs.date ASC")
	List<RecoveryScore> findByUserIdAndDateBetween(
		@Param("userId") Long userId,
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate
	);
}
