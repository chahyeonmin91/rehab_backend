package com.rehab.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.rehab.domain.entity.DailySummary;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일별 활동 이력")
public class ActivityHistoryDto {

	@Schema(description = "날짜", example = "2025-12-01")
	private LocalDateTime dateTime;

	@Schema(description = "활동 달성 여부 (운동 ≥60% or 복약 ≥70%)", example = "true")
	private Boolean isActive;

	@Schema(description = "운동 완료율 (%)", example = "75")
	private Integer exerciseCompletionRate;

	@Schema(description = "복약 완료율 (%)", example = "100")
	private Integer medicationCompletionRate;

	/**
	 * DailySummary → ActivityHistoryDto 변환
	 */
	public static ActivityHistoryDto from(DailySummary summary) {
		boolean isActive = (summary.getExerciseCompletionRate() != null && summary.getExerciseCompletionRate() >= 60)
			|| (summary.getMedicationCompletionRate() != null && summary.getMedicationCompletionRate() >= 70);

		return ActivityHistoryDto.builder()
			.dateTime(summary.getDate())
			.isActive(isActive)
			.exerciseCompletionRate(summary.getExerciseCompletionRate())
			.medicationCompletionRate(summary.getMedicationCompletionRate())
			.build();
	}

	/**
	 * 특정 날짜의 빈 이력 생성 (데이터 없는 날용)
	 */
	public static ActivityHistoryDto createEmpty(LocalDateTime dateTime) {
		return ActivityHistoryDto.builder()
			.dateTime(dateTime)
			.isActive(false)
			.exerciseCompletionRate(0)
			.medicationCompletionRate(0)
			.build();
	}
}
