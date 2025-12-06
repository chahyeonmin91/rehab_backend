package com.rehab.dto.dailySummary;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일일 요약 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySummaryResponse {

	private Long summaryId;
	private Long userId;
	private LocalDate date;
	private Boolean allExercisesCompleted;
	private Integer exerciseCompletionRate;
	private Boolean allMedicationsTaken;
	private Integer medicationCompletionRate;
	private Integer avgPainScore;
	private Integer totalDurationSec;
	private JsonNode dailyMetrics; // JSON 데이터
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}


