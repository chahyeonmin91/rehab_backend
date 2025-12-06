package com.rehab.dto.exercise;

import com.fasterxml.jackson.databind.JsonNode;
import com.rehab.domain.entity.enums.Difficulty;
import com.rehab.domain.entity.enums.EvidenceLevel;
import com.rehab.domain.entity.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 운동 상세 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDetailResponse {

	private Long exerciseId;
	private String title;
	private String description;
	private String bodyPart;
	private Difficulty difficulty;
	private JsonNode contraindications; // JSON
	private JsonNode progressionRules; // JSON
	private EvidenceLevel evidenceLevel;
	private List<ExerciseImageResponse> images;
	private List<ExerciseMediaResponse> media;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ExerciseImageResponse {
		private Long exerciseImageId;
		private String title;
		private JsonNode imageUrl; // JSON
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ExerciseMediaResponse {
		private Long exerciseMediaId;
		private String url;
		private MediaType mediaType;
		private String language;
		private Integer duration;
	}
}






