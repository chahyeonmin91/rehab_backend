package com.rehab.dto.diet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 식단 로그 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 로그 응답")
public class DietLogResponse {

	@Schema(description = "식단 로그 ID", example = "1")
	private Long dietLogId;

	@Schema(description = "사용자 ID", example = "1")
	private Long userId;

	@Schema(description = "식단 플랜 항목 ID", example = "1")
	private Long dietPlanItemId;

	@Schema(description = "식단 제목", example = "닭가슴살 샐러드")
	private String dietTitle;

	@Schema(description = "식사 시간")
	private LocalDateTime loggedAt;

	@Schema(description = "식사 완료 여부", example = "true")
	private Boolean completed;

	@Schema(description = "섭취 비율 (0-100%)", example = "100")
	private Integer portionConsumed;

	@Schema(description = "메모", example = "아침 식사로 완벽히 섭취")
	private String notes;

	@Schema(description = "생성일시")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시")
	private LocalDateTime updatedAt;
}
