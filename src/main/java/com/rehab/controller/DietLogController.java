package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.diet.CreateDietLogRequest;
import com.rehab.dto.diet.DietLogResponse;
import com.rehab.service.dietService.DietLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/diet-logs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "식단 로그 API", description = "식단 로그 관리 API")
public class DietLogController {

	private final DietLogService dietLogService;

	@PostMapping
	@Operation(summary = "식단 로그 생성", description = "식단 로그를 생성합니다. 생성 시 자동으로 일일 요약이 업데이트됩니다.")
	public ApiResponse<DietLogResponse> createDietLog(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam Long userId,
		@Parameter(description = "식단 로그 생성 요청", required = true)
		@Valid @RequestBody CreateDietLogRequest request) {

		log.info("POST /api/v1/diet-logs - userId: {}, dietPlanItemId: {}", userId, request.getDietPlanItemId());

		DietLogResponse response = dietLogService.createDietLog(userId, request);

		return ApiResponse.onSuccess(response);
	}

	@GetMapping
	@Operation(summary = "식단 로그 조회", description = "특정 기간의 식단 로그를 조회합니다.")
	public ApiResponse<List<DietLogResponse>> getDietLogs(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam Long userId,
		@Parameter(description = "시작 날짜", required = true)
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
		@Parameter(description = "종료 날짜", required = true)
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

		log.info("GET /api/v1/diet-logs - userId: {}, start: {}, end: {}", userId, startDate, endDate);

		List<DietLogResponse> response = dietLogService.getDietLogsByDateRange(userId, startDate, endDate);

		return ApiResponse.onSuccess(response);
	}
}
