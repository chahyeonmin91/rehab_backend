package com.rehab.dto.plan;

import com.fasterxml.jackson.databind.JsonNode;
import com.rehab.domain.entity.enums.PlanItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 복약 플랜 항목 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "복약 플랜 항목 응답")
public class MedicationPlanItemResponse {

	@Schema(description = "복약 플랜 항목 ID", example = "1")
	private Long medicationPlanItemId;

	@Schema(description = "약물 ID", example = "301")
	private Long medicationId;

	@Schema(description = "약물명", example = "타이레놀")
	private String medicationName;

	@Schema(description = "용량", example = "500mg")
	private String dose;

	@Schema(description = "상태", example = "ACTIVE")
	private PlanItemStatus status;

	@Schema(description = "순서", example = "1")
	private Integer orderIndex;

	@Schema(description = "시작일", example = "2025-12-01")
	private LocalDate startDate;

	@Schema(description = "종료일", example = "2025-12-31")
	private LocalDate endDate;

	@Schema(description = "추천 이유 (JSON)")
	private JsonNode recommendationReason;

	@Schema(description = "생성일시")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시")
	private LocalDateTime updatedAt;
}
