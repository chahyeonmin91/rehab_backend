package com.rehab.domain.repository.plan;

import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.enums.PlanPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PlanItem Repository
 */
@Repository
public interface PlanItemRepository extends JpaRepository<PlanItem, Long> {

	/**
	 * 특정 플랜의 모든 항목 조회 (순서대로)
	 */
	@Query("SELECT pi FROM PlanItem pi " +
		"WHERE pi.rehabPlan.rehabPlanId = :rehabPlanId " +
		"ORDER BY pi.orderIndex ASC")
	List<PlanItem> findByRehabPlanIdOrderByOrderIndex(@Param("rehabPlanId") Long rehabPlanId);

	/**
	 * 특정 플랜의 특정 단계 항목 조회
	 */
	@Query("SELECT pi FROM PlanItem pi " +
		"WHERE pi.rehabPlan.rehabPlanId = :rehabPlanId " +
		"AND pi.phase = :phase " +
		"ORDER BY pi.orderIndex ASC")
	List<PlanItem> findByRehabPlanIdAndPhase(
		@Param("rehabPlanId") Long rehabPlanId,
		@Param("phase") PlanPhase phase
	);

	/**
	 * 특정 플랜의 항목 개수 조회
	 */
	long countByRehabPlan_RehabPlanId(Long rehabPlanId);

	/**
	 * 특정 재활 플랜의 전체 운동 항목 개수 조회
	 */
	int countByRehabPlan(RehabPlan rehabPlan);
}
