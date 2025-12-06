package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.dailySummary.DailySummaryResponse;
import com.rehab.service.dailySummary.DailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 일일 요약 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/daily-summary")
@RequiredArgsConstructor
@Tag(name = "일일 요약", description = "일일 요약 관련 API")
public class DailySummaryController {

	private final DailySummaryService dailySummaryService;

	/**
	 * 3.6 일일 요약 조회
	 */
	@GetMapping
	@Operation(summary = "일일 요약 조회", description = "특정 날짜의 운동/복약 완료율 및 통증 요약을 조회합니다.")
	public ApiResponse<DailySummaryResponse> getDailySummary(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam("userId") Long userId,

		@Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true)
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		log.info("API 호출: 일일 요약 조회 - userId: {}, date: {}", userId, date);
		DailySummaryResponse response = dailySummaryService.getDailySummary(userId, date);
		return ApiResponse.onSuccess(response);
	}
}
