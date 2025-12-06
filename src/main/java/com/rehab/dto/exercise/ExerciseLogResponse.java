package com.rehab.dto.exercise;

import com.rehab.domain.entity.enums.ExerciseLogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 운동 로그 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogResponse {

	private Long exerciseLogId;
	private Long userId;
	private Long planItemId;
	private LocalDateTime loggedAt;
	private Integer painBefore;
	private Integer painAfter;
	private Integer rpe;
	private Integer completionRate;
	private Integer durationSec;
	private String notes;
	private ExerciseLogStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
