package com.rehab.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 회복 예측 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회복 예측 응답")
public class RecoveryPredictionResponse {

	@Schema(description = "예측 정보")
	private Prediction prediction;

	@Schema(description = "영향 요인 목록")
	private List<Factor> factors;

	@Schema(description = "모델 정보")
	private ModelInfo modelInfo;

	@Schema(description = "AI 추론 로그 ID")
	private Long aiInferenceLogId;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "예측 정보")
	public static class Prediction {

		@Schema(description = "7일 후 예상 통증 수준", example = "4.5")
		private Double expectedPainLevel7d;

		@Schema(description = "14일 후 예상 통증 수준", example = "3.2")
		private Double expectedPainLevel14d;

		@Schema(description = "회복 확률 (%)", example = "78.5")
		private Double recoveryProbability;

		@Schema(description = "목표 달성까지 예상 일수", example = "18")
		private Integer estimatedDaysToTarget;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "영향 요인")
	public static class Factor {

		@Schema(description = "요인", example = "ADHERENCE")
		private String factor;

		@Schema(description = "영향도", example = "HIGH")
		private String impact;

		@Schema(description = "설명", example = "높은 완료율이 빠른 회복을 돕습니다")
		private String description;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "모델 정보")
	public static class ModelInfo {

		@Schema(description = "모델 키", example = "recovery-predictor-v0")
		private String modelKey;

		@Schema(description = "모델 버전", example = "1.0.0")
		private String modelVer;
	}
}
