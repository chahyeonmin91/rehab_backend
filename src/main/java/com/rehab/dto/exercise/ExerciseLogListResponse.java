package com.rehab.dto.exercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 운동 로그 목록 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogListResponse {

	private LocalDateTime date;
	private List<ExerciseLogResponse> logs;
}







