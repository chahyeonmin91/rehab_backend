package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.enums.MealTime;
import com.rehab.domain.entity.enums.PlanPhase;
import com.rehab.dto.plan.*;
import com.rehab.service.rehabPlan.RehabPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
	@Operation(summary = "현재 활성 플랜 조회", description = "사용자의 현재 활성화된 재활 플랜 중 가장 최신 플랜을 조회합니다.")
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
		log.info("API 호출: 플랜 운동 항목 조회 - rehabPlanId: {}, date: {}, phase: {}", rehabPlanId, date, phase);
		PlanItemListResponse response = rehabPlanService.getPlanItems(rehabPlanId, date, phase);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.3 사용자의 모든 재활 플랜 조회
	 */
	@GetMapping("/plans")
	@Operation(summary = "사용자의 운동 재활 플랜 조회", description = "사용자의 전체 재활 플랜 목록을 조회합니다.")
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

	/**
	 * 3.4 플랜별 복약 항목 조회 (신규)
	 */
	@GetMapping("/plans/{rehabPlanId}/medications")
	@Operation(summary = "플랜별 복약 항목 조회", description = "특정 재활 플랜의 복약 항목들을 조회합니다.")
	public ApiResponse<List<MedicationPlanItemResponse>> getMedicationPlanItems(
		@Parameter(description = "재활 플랜 ID", required = true)
		@PathVariable Long rehabPlanId
	) {
		log.info("API 호출: 플랜 복약 항목 조회 - rehabPlanId: {}", rehabPlanId);
		List<MedicationPlanItemResponse> response = rehabPlanService.getMedicationPlanItems(rehabPlanId);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.5 플랜별 식단 항목 조회 (신규)
	 */
	@GetMapping("/plans/{rehabPlanId}/diets")
	@Operation(summary = "플랜별 식단 항목 조회", description = "특정 재활 플랜의 식단 항목들을 조회합니다.")
	public ApiResponse<List<DietPlanItemResponse>> getDietPlanItems(
		@Parameter(description = "재활 플랜 ID", required = true)
		@PathVariable Long rehabPlanId,

		@Parameter(description = "식사 시간 필터 (BREAKFAST, LUNCH, DINNER, SNACK)")
		@RequestParam(required = false) MealTime mealTime
	) {
		log.info("API 호출: 플랜 식단 항목 조회 - rehabPlanId: {}, mealTime: {}", rehabPlanId, mealTime);
		List<DietPlanItemResponse> response = rehabPlanService.getDietPlanItems(rehabPlanId, mealTime);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 3.6 플랜의 모든 항목 통합 조회 (신규)
	 */
	@GetMapping("/plans/{rehabPlanId}/all-items")
	@Operation(summary = "플랜의 모든 항목 통합 조회",
		description = "특정 재활 플랜의 운동, 복약, 식단 항목을 모두 조회합니다.")
	public ApiResponse<AllPlanItemsResponse> getAllPlanItems(
		@Parameter(description = "재활 플랜 ID", required = true)
		@PathVariable Long rehabPlanId
	) {
		log.info("API 호출: 플랜 전체 항목 조회 - rehabPlanId: {}", rehabPlanId);
		AllPlanItemsResponse response = rehabPlanService.getAllPlanItems(rehabPlanId);
		return ApiResponse.onSuccess(response);
	}

	@PostMapping
	@Operation(
		summary = "재활 플랜 통합 생성",
		description = "운동, 복약, 식단을 포함한 재활 플랜을 한 번에 생성합니다. " +
			"각 항목은 선택적으로 포함할 수 있으며, 빈 리스트로 전달 시 해당 항목 없이 플랜이 생성됩니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "플랜 생성 성공",
			content = @Content(schema = @Schema(implementation = RehabPlanDetailResponse.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (유효성 검증 실패)"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "사용자, 운동, 복약, 식단 정보를 찾을 수 없음"
		)
	})
	public ApiResponse<RehabPlanDetailResponse> createRehabPlan(
		@Parameter(description = "사용자 ID", required = true, example = "1")
		@RequestParam Long userId,
		@Parameter(description = "재활 플랜 생성 요청", required = true)
		@Valid @RequestBody CreateRehabPlanRequest request) {

		log.info("POST /api/v1/rehab/plans - userId: {}, title: {}", userId, request.getTitle());

		RehabPlanDetailResponse response = rehabPlanService.createRehabPlanWithItems(userId, request);

		return ApiResponse.onSuccess(response);
	}
}







