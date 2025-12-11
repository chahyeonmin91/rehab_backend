package com.rehab.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 플랜의 모든 항목 통합 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "플랜의 모든 항목 통합 조회 응답")
public class AllPlanItemsResponse {

	@Schema(description = "재활 플랜 ID", example = "789")
	private Long rehabPlanId;

	@Schema(description = "플랜 제목", example = "허리 통증 4주 종합 재활 플랜")
	private String title;

	@Schema(description = "운동 항목 목록")
	private List<PlanItemResponse> exercises;

	@Schema(description = "복약 항목 목록")
	private List<MedicationPlanItemResponse> medications;

	@Schema(description = "식단 항목 목록")
	private List<DietPlanItemResponse> diets;

	@Schema(description = "총 항목 수")
	private Integer totalCount;
}
