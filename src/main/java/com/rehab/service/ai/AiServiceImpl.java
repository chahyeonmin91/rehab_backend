package com.rehab.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.config.AiModelConfig;
import com.rehab.domain.entity.AiInferenceLog;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.ai.AiInferenceLogRepository;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.ai.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 서비스
 * 학과 서버의 허깅페이스 모델 엔드포인트와 HTTP 통신
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiServiceImpl implements AiService {

	private final RestTemplate aiModelRestTemplate;
	private final AiModelConfig aiModelConfig;
	private final AiInferenceLogRepository aiInferenceLogRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 8.1 운동 추천 요청
	 * 학과 서버의 허깅페이스 모델 호출
	 */
	@Transactional
	public AiRecommendationResponse recommendExercises(Long userId, AiRecommendationRequest request) {
		log.info("AI 운동 추천 요청 - userId: {}, painLevel: {}, targetArea: {}",
			userId, request.getContext().getCurrentPainLevel(), request.getContext().getTargetArea());

		long startTime = System.currentTimeMillis();

		try {
			// 1. 사용자 조회
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

			// 2. AI 모델 서버 호출
			String url = aiModelConfig.getAiModelBaseUrl() + "/api/v1/recommendations";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<AiRecommendationRequest> entity = new HttpEntity<>(request, headers);

			log.info("AI 모델 서버 호출 - URL: {}", url);

			ResponseEntity<AiRecommendationResponse> response = aiModelRestTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				AiRecommendationResponse.class
			);

			AiRecommendationResponse aiResponse = response.getBody();
			if (aiResponse == null) {
				throw new RehabPlanException(ErrorStatus.AI_INFERENCE_FAILED);
			}

			// 3. 추론 로그 저장
			long latencyMs = System.currentTimeMillis() - startTime;
			log.info("AI 추론 완료 - latency: {}ms", latencyMs);

			AiInferenceLog inferenceLog = saveInferenceLog(
				user,
				"rehab-recommender-v0",
				"1.0.0",
				request,
				aiResponse,
				latencyMs
			);

			// 4. 응답에 로그 ID 추가
			aiResponse.setAiInferenceLogId(inferenceLog.getInferenceLogId());

			return aiResponse;

		} catch (RestClientException e) {
			log.error("AI 모델 서버 통신 실패 - userId: {}", userId, e);
			throw new RehabPlanException(ErrorStatus.AI_SERVER_UNAVAILABLE);
		} catch (Exception e) {
			log.error("AI 추론 실패 - userId: {}", userId, e);
			throw new RehabPlanException(ErrorStatus.AI_INFERENCE_FAILED);
		}
	}

	/**
	 * 8.2 회복 예측 요청
	 * 학과 서버의 허깅페이스 모델 호출
	 */
	@Transactional
	public RecoveryPredictionResponse predictRecovery(Long userId, RecoveryPredictionRequest request) {
		log.info("AI 회복 예측 요청 - userId: {}, painLevel: {}, adherence: {}",
			userId, request.getCurrentPainLevel(), request.getRecentAdherence());

		long startTime = System.currentTimeMillis();

		try {
			// 1. 사용자 조회
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

			// 2. AI 모델 서버 호출
			String url = aiModelConfig.getAiModelBaseUrl() + "/api/v1/predictions/recovery";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RecoveryPredictionRequest> entity = new HttpEntity<>(request, headers);

			log.info("AI 모델 서버 호출 - URL: {}", url);

			ResponseEntity<RecoveryPredictionResponse> response = aiModelRestTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				RecoveryPredictionResponse.class
			);

			RecoveryPredictionResponse aiResponse = response.getBody();
			if (aiResponse == null) {
				throw new RehabPlanException(ErrorStatus.AI_INFERENCE_FAILED);
			}

			// 3. 추론 로그 저장
			long latencyMs = System.currentTimeMillis() - startTime;
			log.info("AI 예측 완료 - latency: {}ms", latencyMs);

			AiInferenceLog inferenceLog = saveInferenceLog(
				user,
				"recovery-predictor-v0",
				"1.0.0",
				request,
				aiResponse,
				latencyMs
			);

			// 4. 응답에 로그 ID 추가
			aiResponse.setAiInferenceLogId(inferenceLog.getInferenceLogId());

			return aiResponse;

		} catch (RestClientException e) {
			log.error("AI 모델 서버 통신 실패 - userId: {}", userId, e);
			throw new RehabPlanException(ErrorStatus.AI_SERVER_UNAVAILABLE);
		} catch (Exception e) {
			log.error("AI 예측 실패 - userId: {}", userId, e);
			throw new RehabPlanException(ErrorStatus.AI_INFERENCE_FAILED);
		}
	}

	/**
	 * 8.3 AI 추론 로그 조회
	 */
	public AiInferenceLogResponse getInferenceLogs(Long userId, String modelKey, Integer limit) {
		log.info("AI 추론 로그 조회 - userId: {}, modelKey: {}, limit: {}", userId, modelKey, limit);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		int pageSize = (limit != null && limit > 0) ? limit : 20;
		PageRequest pageRequest = PageRequest.of(0, pageSize);

		List<AiInferenceLog> logs;
		if (modelKey != null && !modelKey.isEmpty()) {
			logs = aiInferenceLogRepository.findByUserAndModelKey(user, modelKey, pageRequest);
		} else {
			logs = aiInferenceLogRepository.findByUser(user, pageRequest);
		}

		List<AiInferenceLogResponse.InferenceLog> logResponses = logs.stream()
			.map(this::convertToLogResponse)
			.collect(Collectors.toList());

		return AiInferenceLogResponse.builder()
			.logs(logResponses)
			.build();
	}

	// === Private Helper Methods ===

	/**
	 * 추론 로그 저장
	 */
	private AiInferenceLog saveInferenceLog(
		User user,
		String modelKey,
		String modelVersion,
		Object inputData,
		Object outputData,
		long latencyMs
	) {
		try {
			String inputSnapshot = objectMapper.writeValueAsString(inputData);
			String outputSnapshot = objectMapper.writeValueAsString(outputData);

			AiInferenceLog log = AiInferenceLog.builder()
				.user(user)
				.modelKey(modelKey)
				.modelVersion(modelVersion)
				.inputSnapshot(inputSnapshot)
				.outputSnapshot(outputSnapshot)
				.latencyMs((int) latencyMs)
				.knowledgeReferences("[]")
				.build();

			return aiInferenceLogRepository.save(log);

		} catch (JsonProcessingException e) {
			log.error("추론 로그 저장 실패", e);
			throw new RehabPlanException(ErrorStatus.AI_INFERENCE_FAILED);
		}
	}

	/**
	 * AiInferenceLog -> InferenceLog 변환
	 */
	private AiInferenceLogResponse.InferenceLog convertToLogResponse(AiInferenceLog log) {
		JsonNode inputSnapshot = parseJson(log.getInputSnapshot());
		JsonNode outputSnapshot = parseJson(log.getOutputSnapshot());
		JsonNode knowledgeReferences = parseJson(log.getKnowledgeReferences());

		int knowledgeRefCount = (knowledgeReferences != null && knowledgeReferences.isArray())
			? knowledgeReferences.size() : 0;

		return AiInferenceLogResponse.InferenceLog.builder()
			.aiInferenceLogId(log.getInferenceLogId())
			.userId(log.getUser().getUserId())
			.modelKey(log.getModelKey())
			.modelVer(log.getModelVersion())
			.inputSnapshot(inputSnapshot)
			.outputSnapshot(outputSnapshot)
			.latencyMs(log.getLatencyMs())
			.knowledgeReferences(knowledgeRefCount)
			.createdAt(log.getCreatedAt())
			.build();
	}

	/**
	 * JSON 문자열 파싱
	 */
	private JsonNode parseJson(String jsonString) {
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readTree(jsonString);
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 실패: {}", jsonString, e);
			return null;
		}
	}
}
