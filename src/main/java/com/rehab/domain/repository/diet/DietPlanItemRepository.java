package com.rehab.domain.repository.diet;

import com.rehab.domain.entity.DietPlanItem;
import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.enums.MealTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DietPlanItem Repository
 */
@Repository
public interface DietPlanItemRepository extends JpaRepository<DietPlanItem, Long> {

	/**
	 * 재활 플랜별 식단 항목 조회 (순서대로)
	 */
	List<DietPlanItem> findByRehabPlanOrderByOrderIndex(RehabPlan rehabPlan);
	List<DietPlanItem> findByRehabPlan_RehabPlanIdOrderByOrderIndex(Long rehabPlanId);

	/**
	 * 재활 플랜 ID로 식단 항목 조회
	 */
	@Query("SELECT d FROM DietPlanItem d " +
		"JOIN FETCH d.diet " +
		"WHERE d.rehabPlan.rehabPlanId = :rehabPlanId " +
		"ORDER BY d.orderIndex ASC")
	List<DietPlanItem> findByRehabPlanIdWithDiet(@Param("rehabPlanId") Long rehabPlanId);

	/**
	 * 재활 플랜 + 식사 시간으로 조회
	 */
	List<DietPlanItem> findByRehabPlanAndMealTimeOrderByOrderIndex(
		RehabPlan rehabPlan,
		MealTime mealTime
	);

	/**
	 * 재활 플랜의 식단 항목 개수
	 */
	int countByRehabPlan(RehabPlan rehabPlan);

	long countByRehabPlan_RehabPlanId(Long rehabPlanId);
}
