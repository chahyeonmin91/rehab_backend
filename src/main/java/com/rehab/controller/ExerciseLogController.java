package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.exercise.CreateExerciseLogRequest;
import com.rehab.dto.exercise.ExerciseLogListResponse;
import com.rehab.dto.exercise.ExerciseLogResponse;
import com.rehab.service.exercise.ExerciseLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 운동 로그 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/exercise-logs")
@RequiredArgsConstructor
@Tag(name = "운동 로그", description = "운동 로그 관련 API")
public class ExerciseLogController {

	private final ExerciseLogService exerciseLogService;

	/**
	 * 3.4 운동 로그 생성
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "운동 로그 생성", description = "운동 수행 기록을 생성합니다.")
	public ApiResponse<ExerciseLogResponse> createExerciseLog(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam("userId") Long userId,

		@Parameter(description = "운동 로그 생성 요청", required = true)
		@Valid @RequestBody CreateExerciseLogRequest request
	) {
		log.info("API 호출: 운동 로그 생성 - userId: {}, planItemId: {}", userId, request.getPlanItemId());
		ExerciseLogResponse response = exerciseLogService.createExerciseLog(userId, request);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.5 특정 날짜 운동 로그 조회
	 */
	@GetMapping
	@Operation(summary = "특정 날짜 운동 로그 조회", description = "특정 날짜의 운동 로그 목록을 조회합니다.")
	public ApiResponse<ExerciseLogListResponse> getExerciseLogs(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam("userId") Long userId,

		@Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true)
		@RequestParam
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		log.info("API 호출: 운동 로그 조회 - userId: {}, date: {}", userId, date);
		ExerciseLogListResponse response = exerciseLogService.getExerciseLogsByDate(userId, date);
		return ApiResponse.onSuccess(response);
	}
}






