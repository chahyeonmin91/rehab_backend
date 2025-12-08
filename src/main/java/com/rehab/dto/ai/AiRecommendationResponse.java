package com.rehab.dto.ai;

import com.rehab.domain.entity.enums.Difficulty;
import com.rehab.domain.entity.enums.EvidenceLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * AI 운동 추천 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 운동 추천 응답")
public class AiRecommendationResponse {

	@Schema(description = "추천 운동 목록")
	private List<RecommendedExercise> recommendations;

	@Schema(description = "모델 정보")
	private ModelInfo modelInfo;

	@Schema(description = "AI 추론 로그 ID")
	private Long aiInferenceLogId;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "추천 운동 정보")
	public static class RecommendedExercise {

		@Schema(description = "운동 ID", example = "50")
		private Long exerciseId;

		@Schema(description = "운동 제목", example = "골반 기울이기")
		private String title;

		@Schema(description = "신체 부위", example = "LOWER_BACK")
		private String bodyPart;

		@Schema(description = "난이도")
		private Difficulty difficulty;

		@Schema(description = "추천 이유", example = "현재 통증 수준에 적합하며, 허리 근력 강화에 효과적입니다")
		private String reason;

		@Schema(description = "기대 효과", example = "통증 감소 및 코어 근력 향상")
		private String expectedBenefit;

		@Schema(description = "운동량 제안")
		private DoseSuggestion doseSuggestion;

		@Schema(description = "근거 수준")
		private EvidenceLevel evidenceLevel;

		@Schema(description = "의학 지식 참조 ID 목록")
		private List<Long> knowledgeReferences;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "운동량 제안")
	public static class DoseSuggestion {

		@Schema(description = "세트 수", example = "3")
		private Integer sets;

		@Schema(description = "반복 횟수", example = "10")
		private Integer reps;

		@Schema(description = "휴식 시간 (초)", example = "30")
		private Integer restSeconds;

		@Schema(description = "홀드 시간 (초)", example = "5")
		private Integer holdSeconds;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "모델 정보")
	public static class ModelInfo {

		@Schema(description = "모델 키", example = "rehab-recommender-v0")
		private String modelKey;

		@Schema(description = "모델 버전", example = "1.0.0")
		private String modelVer;
	}
}
