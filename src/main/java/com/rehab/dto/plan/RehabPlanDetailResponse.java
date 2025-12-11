package com.rehab.dto.plan;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rehab.domain.entity.enums.RehabPlanStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "재활 플랜 상세 응답")
public class RehabPlanDetailResponse {

	@Schema(description = "플랜 ID", example = "1")
	private Long rehabPlanId;

	@Schema(description = "사용자 ID", example = "1")
	private Long userId;

	@Schema(description = "플랜 제목", example = "허리 통증 4주 종합 재활 플랜")
	private String title;

	@Schema(description = "상태", example = "ACTIVE")
	private RehabPlanStatus status;

	@Schema(description = "시작 날짜")
	private LocalDateTime startDate;

	@Schema(description = "종료 날짜")
	private LocalDateTime endDate;

	@Schema(description = "메타 정보 (JSON)")
	private String meta;

	@Schema(description = "생성 방식", example = "AI_RECOMMENDATION")
	private String generatedBy;

	@Schema(description = "운동 항목 리스트")
	private List<PlanItemResponse> exerciseItems;

	@Schema(description = "복약 항목 리스트")
	private List<MedicationPlanItemResponse> medicationItems;

	@Schema(description = "식단 항목 리스트")
	private List<DietPlanItemResponse> dietItems;

	@Schema(description = "총 항목 수")
	private Integer totalItemCount;

	@Schema(description = "생성일시")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시")
	private LocalDateTime updatedAt;
}
