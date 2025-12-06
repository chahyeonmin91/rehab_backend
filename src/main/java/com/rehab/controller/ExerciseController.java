package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.exercise.ExerciseDetailResponse;
import com.rehab.service.exercise.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 운동 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "운동", description = "운동 정보 관련 API")
public class ExerciseController {

	private final ExerciseService exerciseService;

	/**
	 * 3.3 운동 상세 정보 조회
	 */
	@GetMapping("/{exerciseId}")
	@Operation(summary = "운동 상세 정보 조회", description = "특정 운동의 상세 정보를 조회합니다.")
	public ApiResponse<ExerciseDetailResponse> getExerciseDetail(
		@Parameter(description = "운동 ID", required = true)
		@PathVariable Long exerciseId
	) {
		log.info("API 호출: 운동 상세 정보 조회 - exerciseId: {}", exerciseId);
		ExerciseDetailResponse response = exerciseService.getExerciseDetail(exerciseId);
		return ApiResponse.onSuccess(response);
	}
}







