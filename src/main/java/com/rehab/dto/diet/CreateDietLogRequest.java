package com.rehab.dto.diet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 식단 로그 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 로그 생성 요청")
public class CreateDietLogRequest {

	@NotNull(message = "식단 플랜 항목 ID는 필수입니다")
	@Schema(description = "식단 플랜 항목 ID", example = "1")
	private Long dietPlanItemId;

	@NotNull(message = "식사 시간은 필수입니다")
	@Schema(description = "식사 시간", example = "2025-12-11T12:00:00")
	private LocalDateTime loggedAt;

	@Schema(description = "식사 완료 여부", example = "true")
	private Boolean completed;

	@Min(value = 0, message = "섭취 비율은 0 이상이어야 합니다")
	@Max(value = 100, message = "섭취 비율은 100 이하여야 합니다")
	@Schema(description = "섭취 비율 (0-100%)", example = "100")
	private Integer portionConsumed;

	@Schema(description = "메모", example = "아침 식사로 완벽히 섭취")
	private String notes;
}
