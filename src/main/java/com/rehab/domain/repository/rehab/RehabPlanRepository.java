package com.rehab.domain.repository.rehab;

import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.RehabPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RehabPlan Repository
 */
@Repository
public interface RehabPlanRepository extends JpaRepository<RehabPlan, Long> {

	/**
	 * 사용자의 현재 활성 플랜 조회
	 * ACTIVE 상태인 가장 최근 플랜
	 * JPQL에서는 LIMIT을 사용할 수 없으므로 First 키워드 사용
	 */
	Optional<RehabPlan> findFirstByUser_UserIdAndStatusOrderByCreatedAtDesc(
		Long userId,
		RehabPlanStatus status
	);

	/**
	 * 사용자 ID로 플랜 존재 여부 확인
	 */
	boolean existsByUser_UserId(Long userId);

	/**
	 * 사용자의 모든 플랜 조회 (최신순)
	 */
	List<RehabPlan> findByUserOrderByCreatedAtDesc(User user);

	/**
	 * 상태별 조회 - enum 타입
	 */
	List<RehabPlan> findByUserAndStatusOrderByCreatedAtDesc(User user, RehabPlanStatus status);
}
