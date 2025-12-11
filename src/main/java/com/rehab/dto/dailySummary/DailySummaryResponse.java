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

	// 운동 관련
	private Boolean allExercisesCompleted;
	private Integer exerciseCompletionRate;

	// 복약 관련
	private Boolean allMedicationsTaken;
	private Integer medicationCompletionRate;

	// 식단 관련
	private Boolean allDietCompleted;
	private Integer dietCompletionRate;

	// 통증 및 운동 시간
	private Integer avgPainScore;
	private Integer totalDurationSec;

	// 메트릭스
	private JsonNode dailyMetrics; // JSON 데이터

	// 타임스탬프
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}


