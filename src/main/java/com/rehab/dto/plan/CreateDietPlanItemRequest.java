package com.rehab.dto.plan;

import com.rehab.domain.entity.enums.MealTime;
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
@Schema(description = "식단 항목 생성 요청")
public class CreateDietPlanItemRequest {

	@NotNull(message = "식단 ID는 필수입니다")
	@Schema(description = "식단 ID", example = "1")
	private Long dietId;

	@Schema(description = "식사 시간", example = "BREAKFAST", allowableValues = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"})
	private MealTime mealTime;

	@Builder.Default
	@Schema(description = "1인분 기준 섭취량", example = "1인분")
	private String portion = "1인분";

	@Builder.Default
	@Schema(description = "상태", example = "ACTIVE", allowableValues = {"ACTIVE", "PAUSED", "COMPLETED", "SKIPPED"})
	private PlanItemStatus status = PlanItemStatus.ACTIVE;

	@Schema(description = "순서", example = "1")
	private Integer orderIndex;

	@Schema(description = "추천 이유 (JSON)", example = "{\"reason\":\"단백질 보충을 위한 식단\",\"calories\":450}")
	private String recommendationReason;
}
