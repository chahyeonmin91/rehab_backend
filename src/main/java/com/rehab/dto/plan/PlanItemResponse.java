package com.rehab.dto.plan;

import com.fasterxml.jackson.databind.JsonNode;
import com.rehab.domain.entity.enums.PlanItemStatus;
import com.rehab.domain.entity.enums.RehabPhase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 플랜 항목 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanItemResponse {

	private Long planItemId;
	private Long exerciseId;
	private RehabPhase phase;
	private Integer orderIndex;
	private PlanItemStatus status;
	private JsonNode dose; // JSON 데이터
	private JsonNode recommendationReason; // JSON 데이터
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
