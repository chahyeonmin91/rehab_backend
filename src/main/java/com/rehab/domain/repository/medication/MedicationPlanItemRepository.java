package com.rehab.domain.repository.medication;

import com.rehab.domain.entity.MedicationPlanItem;
import com.rehab.domain.entity.RehabPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MedicationPlanItem Repository
 */
@Repository
public interface MedicationPlanItemRepository extends JpaRepository<MedicationPlanItem, Long> {

	/**
	 * 재활 플랜별 복약 항목 조회 (순서대로)
	 */
	List<MedicationPlanItem> findByRehabPlanOrderByOrderIndex(RehabPlan rehabPlan);

	/**
	 * 재활 플랜 ID로 복약 항목 조회
	 */
	@Query("SELECT m FROM MedicationPlanItem m " +
		"JOIN FETCH m.medication " +
		"WHERE m.rehabPlan.rehabPlanId = :rehabPlanId " +
		"ORDER BY m.orderIndex ASC")
	List<MedicationPlanItem> findByRehabPlanIdWithMedication(@Param("rehabPlanId") Long rehabPlanId);

	/**
	 * 재활 플랜의 복약 항목 개수
	 */
	int countByRehabPlan(RehabPlan rehabPlan);

	long countByRehabPlan_RehabPlanId(Long rehabPlanId);
}
