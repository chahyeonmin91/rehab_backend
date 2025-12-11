package com.rehab.dto.plan;

import java.time.LocalDate;

import com.rehab.domain.entity.enums.PlanItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "복약 항목 생성 요청")
public class CreateMedicationPlanItemRequest {

	@NotNull(message = "복약 ID는 필수입니다")
	@Schema(description = "복약 ID", example = "1")
	private Long medicationId;

	@Builder.Default
	@Schema(description = "상태", example = "ACTIVE", allowableValues = {"ACTIVE", "PAUSED", "COMPLETED", "SKIPPED"})
	private PlanItemStatus status = PlanItemStatus.ACTIVE;

	@Schema(description = "순서", example = "1")
	private Integer orderIndex;

	@Schema(description = "복약 시작일", example = "2025-12-01")
	private LocalDate startDate;

	@Schema(description = "복약 종료일", example = "2025-12-28")
	private LocalDate endDate;

	@Schema(description = "추천 이유 (JSON)", example = "{\"reason\":\"통증 완화를 위한 진통제\",\"dosage\":\"500mg\"}")
	private String recommendationReason;
}
