package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.enums.PlanPhase;
import com.rehab.dto.plan.PlanItemListResponse;
import com.rehab.dto.plan.RehabPlanListResponse;
import com.rehab.dto.plan.RehabPlanResponse;
import com.rehab.service.rehabPlan.RehabPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 재활 플랜 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rehab")
@RequiredArgsConstructor
@Tag(name = "재활 플랜", description = "재활 플랜 관련 API")
public class RehabPlanController {

	private final RehabPlanService rehabPlanService;

	/**
	 * 3.1 현재 활성 플랜 조회
	 */
	@GetMapping("/plans/current")
	@Operation(summary = "현재 활성 플랜 조회", description = "사용자의 현재 활성화된 재활 플랜을 조회합니다.")
	public ApiResponse<RehabPlanResponse> getCurrentPlan(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam("userId") Long userId
	) {
		log.info("API 호출: 현재 활성 플랜 조회 - userId: {}", userId);
		RehabPlanResponse response = rehabPlanService.getCurrentPlan(userId);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.2 플랜별 운동 항목 조회
	 */
	@GetMapping("/plans/{rehabPlanId}/items")
	@Operation(summary = "플랜별 운동 항목 조회", description = "특정 재활 플랜의 운동 항목들을 조회합니다.")
	public ApiResponse<PlanItemListResponse> getPlanItems(
		@Parameter(description = "재활 플랜 ID", required = true)
		@PathVariable Long rehabPlanId,

		@Parameter(description = "조회할 날짜 (YYYY-MM-DD)")
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

		@Parameter(description = "플랜 단계")
		@RequestParam(required = false) PlanPhase phase
	) {
		log.info("API 호출: 플랜 항목 조회 - rehabPlanId: {}, date: {}, phase: {}", rehabPlanId, date, phase);
		PlanItemListResponse response = rehabPlanService.getPlanItems(rehabPlanId, date, phase);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.3 사용자의 모든 재활 플랜 조회
	 */
	@GetMapping("/plans")
	@Operation(summary = "사용자의 모든 재활 플랜 조회", description = "사용자의 전체 재활 플랜 목록을 조회합니다.")
	public ApiResponse<RehabPlanListResponse> getAllPlans(
		@Parameter(description = "사용자 ID", required = true)
		@RequestParam("userId") Long userId,

		@Parameter(description = "플랜 상태 필터 (ACTIVE, INACTIVE, COMPLETED)")
		@RequestParam(required = false) String status
	) {
		log.info("API 호출: 모든 플랜 조회 - userId: {}, status: {}", userId, status);
		RehabPlanListResponse response = rehabPlanService.getAllPlans(userId, status);
		return ApiResponse.onSuccess(response);
	}
}







