package com.rehab.service.ai;


import com.rehab.dto.ai.AiInferenceLogResponse;
import com.rehab.dto.ai.AiRecommendationRequest;
import com.rehab.dto.ai.AiRecommendationResponse;
import com.rehab.dto.ai.RecoveryPredictionRequest;
import com.rehab.dto.ai.RecoveryPredictionResponse;

public interface AiService {

	/**
	 * 8.1 운동 추천 요청
	 * 학과 서버의 허깅페이스 모델 호출
	 */
	AiRecommendationResponse recommendExercises(Long userId, AiRecommendationRequest request);

	/**
	 * 8.2 회복 예측 요청
	 * 학과 서버의 허깅페이스 모델 호출
	 */
	RecoveryPredictionResponse predictRecovery(Long userId, RecoveryPredictionRequest request);

	/**
	 * 8.3 AI 추론 로그 조회
	 */
	AiInferenceLogResponse getInferenceLogs(Long userId, String modelKey, Integer limit);
}
