package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.ai.AiInferenceLogResponse;
import com.rehab.dto.ai.AiRecommendationRequest;
import com.rehab.dto.ai.AiRecommendationResponse;
import com.rehab.dto.ai.RecoveryPredictionRequest;
import com.rehab.dto.ai.RecoveryPredictionResponse;
import com.rehab.service.ai.AiServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * AI 추천 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI 추천", description = "AI 기반 운동 추천 및 회복 예측 API")
public class AiController {

	private final AiServiceImpl aiServiceImpl;

	/**
	 * 8.1 운동 추천 요청
	 */
	@PostMapping("/recommendations")
	@Operation(
		summary = "AI 운동 추천",
		description = "사용자의 현재 상태를 기반으로 AI가 적합한 운동을 추천합니다."
	)
	public ApiResponse<AiRecommendationResponse> recommendExercises(
		Authentication authentication,
		@Valid @RequestBody AiRecommendationRequest request
	) {
		Long userId = extractUserId(authentication);
		log.info("API 호출: AI 운동 추천 - userId: {}, painLevel: {}",
			userId, request.getContext().getCurrentPainLevel());

		AiRecommendationResponse response = aiServiceImpl.recommendExercises(userId, request);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 8.2 회복 예측 요청
	 */
	@PostMapping("/predictions/recovery")
	@Operation(
		summary = "AI 회복 예측",
		description = "사용자의 현재 상태와 순응도를 기반으로 회복 경과를 예측합니다."
	)
	public ApiResponse<RecoveryPredictionResponse> predictRecovery(
		Authentication authentication,
		@Valid @RequestBody RecoveryPredictionRequest request
	) {
		Long userId = extractUserId(authentication);
		log.info("API 호출: AI 회복 예측 - userId: {}, painLevel: {}, adherence: {}",
			userId, request.getCurrentPainLevel(), request.getRecentAdherence());

		RecoveryPredictionResponse response = aiServiceImpl.predictRecovery(userId, request);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * 8.3 AI 추론 로그 조회
	 */
	@GetMapping("/inference-logs")
	@Operation(
		summary = "AI 추론 로그 조회",
		description = "사용자의 AI 추론 이력을 조회합니다."
	)
	public ApiResponse<AiInferenceLogResponse> getInferenceLogs(
		Authentication authentication,
		@Parameter(description = "모델 키 (선택)") @RequestParam(required = false) String modelKey,
		@Parameter(description = "조회 개수 (기본값: 20)") @RequestParam(required = false) Integer limit
	) {
		Long userId = extractUserId(authentication);
		log.info("API 호출: AI 추론 로그 조회 - userId: {}, modelKey: {}, limit: {}",
			userId, modelKey, limit);

		AiInferenceLogResponse response = aiServiceImpl.getInferenceLogs(userId, modelKey, limit);
		return ApiResponse.onSuccess(response);
	}

	/**
	 * Authentication에서 userId 추출
	 */
	private Long extractUserId(Authentication authentication) {
		// TODO: 실제 구현에서는 JWT에서 userId 추출
		// 임시로 하드코딩 (테스트용)
		return 1L;
	}
}
