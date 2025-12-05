package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.User;
import com.rehab.dto.medication.MedicationDto;
import com.rehab.service.medicationService.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medications")
@Tag(name = "Medication", description = "복약 관리 API")
public class MedicationController {

	private final MedicationService medicationService;

	@PostMapping
	@Operation(summary = "복약 등록")
	public ApiResponse<MedicationDto.Response> createMedication(
		@AuthenticationPrincipal User user,
		@RequestBody MedicationDto.CreateRequest request
	) {
		return ApiResponse.onSuccess(medicationService.createMedication(user, request));
	}

	@GetMapping
	@Operation(summary = "내 복약 리스트 조회")
	public ApiResponse<List<MedicationDto.Response>> getMyMedications(
		@AuthenticationPrincipal User user
	) {
		return ApiResponse.onSuccess(medicationService.getMyMedications(user));
	}

	@PostMapping("/{medicationId}/schedule")
	@Operation(summary = "복약 스케줄 추가")
	public ApiResponse<MedicationDto.ScheduleResponse> addSchedule(
		@PathVariable Long medicationId,
		@RequestBody MedicationDto.ScheduleRequest request
	) {
		return ApiResponse.onSuccess(medicationService.addSchedule(medicationId, request));
	}

	@PostMapping("/{medicationId}/log")
	@Operation(summary = "복약 기록 추가")
	public ApiResponse<MedicationDto.MedicationLogResponse> recordLog(
		@PathVariable Long medicationId,
		@RequestBody MedicationDto.LogRequest request
	) {
		return ApiResponse.onSuccess(medicationService.recordLog(medicationId, request));
	}
}
