package com.rehab.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 운동 추천 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 운동 추천 요청")
public class AiRecommendationRequest {

	@Valid
	@NotNull(message = "context는 필수입니다.")
	@Schema(description = "추천 컨텍스트")
	private RecommendationContext context;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "추천 컨텍스트 정보")
	public static class RecommendationContext {

		@NotNull(message = "현재 통증 수준은 필수입니다.")
		@Min(value = 1, message = "통증 수준은 1 이상이어야 합니다.")
		@Max(value = 10, message = "통증 수준은 10 이하여야 합니다.")
		@Schema(description = "현재 통증 수준 (1-10)", example = "6")
		private Integer currentPainLevel;

		@Schema(description = "재활 목표", example = "허리 통증 완화")
		private String goal;

		@Schema(description = "목표 부위", example = "LOWER_BACK")
		private String targetArea;
	}
}







