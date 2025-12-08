package com.rehab.dto.ai;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 추론 로그 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 추론 로그 응답")
public class AiInferenceLogResponse {

	@Schema(description = "로그 목록")
	private List<InferenceLog> logs;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "추론 로그 정보")
	public static class InferenceLog {

		@Schema(description = "AI 추론 로그 ID", example = "801")
		private Long aiInferenceLogId;

		@Schema(description = "사용자 ID", example = "123")
		private Long userId;

		@Schema(description = "모델 키", example = "rehab-recommender-v0")
		private String modelKey;

		@Schema(description = "모델 버전", example = "1.0.0")
		private String modelVer;

		@Schema(description = "입력 스냅샷 (JSON)")
		private JsonNode inputSnapshot;

		@Schema(description = "출력 스냅샷 (JSON)")
		private JsonNode outputSnapshot;

		@Schema(description = "지연 시간 (밀리초)", example = "245")
		private Integer latencyMs;

		@Schema(description = "지식 참조 수", example = "2")
		private Integer knowledgeReferences;

		@Schema(description = "생성 일시")
		private LocalDateTime createdAt;
	}
}
