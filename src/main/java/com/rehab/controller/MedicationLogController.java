package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.medication.CreateMedicationLogRequest;
import com.rehab.dto.medication.MedicationLogListResponse;
import com.rehab.dto.medication.MedicationLogResponse;
import com.rehab.service.medicationService.MedicationLogService;

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

@RestController
@RequestMapping("/api/v1/medication-logs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "복약 로그", description = "복약 로그 관리 API")
public class MedicationLogController {

	private final MedicationLogService medicationLogService;

	/**
	 * 4.5 복약 로그 생성
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "복약 로그 생성", description = "복약 로그를 생성합니다. 생성 시 자동으로 일일 요약이 업데이트됩니다.")
	public ApiResponse<MedicationLogResponse> createMedicationLog(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam Long userId,

		@Parameter(description = "복약 로그 생성 요청", required = true)
		@Valid @RequestBody CreateMedicationLogRequest request) {

		log.info("API 호출: 복약 로그 생성 - userId: {}, medicationId: {}", userId, request.getMedicationId());

		MedicationLogResponse response = medicationLogService.createMedicationLog(userId, request);

		return ApiResponse.onSuccess(response);
	}

	/**
	 * 특정 날짜 복약 로그 조회
	 */
	@GetMapping
	@Operation(summary = "특정 날짜 복약 로그 조회", description = "특정 날짜의 복약 로그 목록을 조회합니다.")
	public ApiResponse<MedicationLogListResponse> getMedicationLogs(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam Long userId,

		@Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true)
		@RequestParam
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		log.info("API 호출: 복약 로그 조회 - userId: {}, date: {}", userId, date);

		MedicationLogListResponse response = medicationLogService.getMedicationLogsByDate(userId, date);

		return ApiResponse.onSuccess(response);
	}
}
