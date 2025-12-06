package com.rehab.dto.exercise;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 운동 로그 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExerciseLogRequest {

	@NotNull(message = "플랜 항목 ID는 필수입니다")
	private Long planItemId;

	@NotNull(message = "기록 시간은 필수입니다")
	private LocalDateTime loggedAt;

	@Min(value = 1, message = "통증 점수는 1-10 사이여야 합니다")
	@Max(value = 10, message = "통증 점수는 1-10 사이여야 합니다")
	private Integer painBefore;

	@Min(value = 1, message = "통증 점수는 1-10 사이여야 합니다")
	@Max(value = 10, message = "통증 점수는 1-10 사이여야 합니다")
	private Integer painAfter;

	@Min(value = 1, message = "RPE는 1-10 사이여야 합니다")
	@Max(value = 10, message = "RPE는 1-10 사이여야 합니다")
	private Integer rpe;

	@Min(value = 0, message = "완료율은 0-100 사이여야 합니다")
	@Max(value = 100, message = "완료율은 0-100 사이여야 합니다")
	private Integer completionRate;

	@Min(value = 0, message = "운동 시간은 0 이상이어야 합니다")
	private Integer durationSec;

	private String notes;
}






