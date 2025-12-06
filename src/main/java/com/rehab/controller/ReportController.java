package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.report.ProgressReportResponse;
import com.rehab.dto.report.ReportSnapshotListResponse;
import com.rehab.dto.report.WeeklyReportResponse;
import com.rehab.service.report.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report", description = "주간 리포트 및 통계 API")
public class ReportController {

	private final ReportService reportService;

	@GetMapping("/progress")
	@Operation(
		summary = "진행률 리포트 조회",
		description = "특정 기간의 운동/복약/통증 진행률 통계를 조회합니다."
	)
	public ApiResponse<ProgressReportResponse> getProgressReport(
		@Parameter(description = "사용자 ID", example = "1", required = true)
		@RequestParam Long userId,

		@Parameter(description = "조회 기간 (7d, 14d, 30d)", example = "7d", required = true)
		@RequestParam String range,

		@Parameter(description = "종료 날짜 (YYYY-MM-DD, 기본값: 오늘)", example = "2025-12-01")
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		log.info("GET /api/v1/reports/progress - userId: {}, range: {}, endDate: {}",
			userId, range, endDate);

		LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
		ProgressReportResponse response = reportService.getProgressReport(userId, range, endDateTime);

		return ApiResponse.onSuccess(response);
	}

	@GetMapping("/weekly")
	@Operation(
		summary = "주간 하이라이트 조회",
		description = "주간 리포트 스냅샷을 조회합니다."
	)
	public ApiResponse<WeeklyReportResponse> getWeeklyReport(
		@Parameter(description = "사용자 ID", example = "1", required = true)
		@RequestParam Long userId,

		@Parameter(description = "주간 시작 날짜 (YYYY-MM-DD, 월요일, 기본값: 이번 주 월요일)",
			example = "2025-11-25")
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate weekStart
	) {
		log.info("GET /api/v1/reports/weekly - userId: {}, weekStart: {}", userId, weekStart);

		WeeklyReportResponse response = reportService.getWeeklyReport(userId, weekStart);

		return ApiResponse.onSuccess(response);
	}

	@GetMapping("/snapshots")
	@Operation(
		summary = "리포트 스냅샷 목록 조회",
		description = "저장된 리포트 스냅샷 목록을 조회합니다."
	)
	public ApiResponse<ReportSnapshotListResponse> getReportSnapshots(
		@Parameter(description = "사용자 ID", example = "1", required = true)
		@RequestParam Long userId,

		@Parameter(description = "기간 타입 (WEEKLY, MONTHLY, 선택 시 전체)",
			example = "WEEKLY")
		@RequestParam(required = false) String period,

		@Parameter(description = "조회 개수 (기본값: 10, 최대: 100)",
			example = "10")
		@RequestParam(required = false) Integer limit
	) {
		log.info("GET /api/v1/reports/snapshots - userId: {}, period: {}, limit: {}",
			userId, period, limit);

		if (limit != null && limit > 100) {
			limit = 100;
		}

		ReportSnapshotListResponse response = reportService.getReportSnapshots(userId, period, limit);

		return ApiResponse.onSuccess(response);
	}
}
