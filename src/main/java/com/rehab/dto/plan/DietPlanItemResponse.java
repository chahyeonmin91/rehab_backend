package com.rehab.dto.plan;

import com.fasterxml.jackson.databind.JsonNode;
import com.rehab.domain.entity.enums.MealTime;
import com.rehab.domain.entity.enums.PlanItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 식단 플랜 항목 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 플랜 항목 응답")
public class DietPlanItemResponse {

	@Schema(description = "식단 플랜 항목 ID", example = "1")
	private Long dietPlanItemId;

	@Schema(description = "식단 ID", example = "10")
	private Long dietId;

	@Schema(description = "식단명", example = "닭가슴살 샐러드")
	private String dietTitle;

	@Schema(description = "식사 시간", example = "BREAKFAST")
	private MealTime mealTime;

	@Schema(description = "섭취 권장량", example = "1인분")
	private String portion;

	@Schema(description = "상태", example = "ACTIVE")
	private PlanItemStatus status;

	@Schema(description = "순서", example = "1")
	private Integer orderIndex;

	@Schema(description = "추천 이유 (JSON)")
	private JsonNode recommendationReason;

	@Schema(description = "생성일시")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시")
	private LocalDateTime updatedAt;
}
