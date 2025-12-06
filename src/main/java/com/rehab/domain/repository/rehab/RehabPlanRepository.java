package com.rehab.domain.repository.rehab;

import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.enums.RehabPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RehabPlan Repository
 */
@Repository
public interface RehabPlanRepository extends JpaRepository<RehabPlan, Long> {

	/**
	 * 사용자의 현재 활성 플랜 조회
	 * ACTIVE 상태인 가장 최근 플랜
	 */
	@Query("SELECT rp FROM RehabPlan rp " +
		"WHERE rp.user.userId = :userId " +
		"AND rp.status = :status " +
		"ORDER BY rp.createdAt DESC " +
		"LIMIT 1")
	Optional<RehabPlan> findCurrentPlanByUserIdAndStatus(
		@Param("userId") Long userId,
		@Param("status") RehabPlanStatus status
	);

	/**
	 * 사용자 ID로 플랜 존재 여부 확인
	 */
	boolean existsByUser_UserId(Long userId);
}
