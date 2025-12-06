package com.rehab.domain.repository.streak;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.rehab.domain.entity.UserStreak;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {

	/**
	 * 사용자 ID로 Streak 조회
	 */
	Optional<UserStreak> findByUserId(Long userId);

	/**
	 * 사용자 ID로 Streak 존재 여부 확인
	 */
	boolean existsByUserId(Long userId);

	/**
	 * 특정 날짜 이전에 마지막 활동한 사용자들의 Streak 조회
	 * - 배치 작업에서 끊긴 streak 정리용
	 */
	@Query("SELECT s FROM UserStreak s WHERE s.lastActiveDate < :date AND s.currentStreak > 0")
	List<UserStreak> findStaleStreaks(@Param("date") LocalDate date);

	/**
	 * 현재 활성 상태인(streak > 0) 사용자 수 조회
	 * - 관리자 대시보드용 통계
	 */
	@Query("SELECT COUNT(s) FROM UserStreak s WHERE s.currentStreak > 0")
	long countActiveStreaks();

	/**
	 * 최대 streak 기준 상위 N명 조회
	 * - 리더보드 기능용 (선택 사항)
	 */
	@Query("SELECT s FROM UserStreak s ORDER BY s.maxStreak DESC, s.currentStreak DESC")
	List<UserStreak> findTopStreaks(@Param("limit") int limit);
}
