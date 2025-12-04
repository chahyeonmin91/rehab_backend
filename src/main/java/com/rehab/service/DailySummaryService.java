package com.rehab.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.DailySummary;
import com.rehab.domain.entity.ExerciseLog;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.User;
import com.rehab.dto.response.DailySummaryResponse;
import com.rehab.repository.DailySummaryRepository;
import com.rehab.repository.ExerciseLogRepository;
import com.rehab.repository.PlanItemRepository;
import com.rehab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 일일 요약 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailySummaryService {

	private final DailySummaryRepository dailySummaryRepository;
	private final ExerciseLogRepository exerciseLogRepository;
	private final PlanItemRepository planItemRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 일일 요약 조회
	 */
	public DailySummaryResponse getDailySummary(Long userId, LocalDate date) {
		log.info("일일 요약 조회 - userId: {}, date: {}", userId, date);

		DailySummary summary = dailySummaryRepository.findByUser_UserIdAndDate(userId, date)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.DAILY_SUMMARY_NOT_FOUND));

		return convertToDailySummaryResponse(summary);
	}

	/**
	 * 일일 요약 업데이트 (운동 로그 생성 시 호출)
	 */
	@Transactional
	public void updateDailySummary(Long userId, LocalDate date) {
		log.info("일일 요약 업데이트 - userId: {}, date: {}", userId, date);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		// 해당 날짜의 운동 로그 조회
		List<ExerciseLog> logs = exerciseLogRepository.findByUserIdAndDate(userId, date);

		if (logs.isEmpty()) {
			log.warn("운동 로그가 없어 일일 요약을 업데이트하지 않습니다.");
			return;
		}

		// 운동 완료율 계산
		long totalExercises = logs.stream()
			.map(ExerciseLog::getPlanItem)
			.map(PlanItem::getRehabPlan)
			.findFirst()
			.map(plan -> planItemRepository.countByRehabPlan_RehabPlanId(plan.getRehabPlanId()))
			.orElse(0L);

		long completedExercises = logs.stream()
			.filter(log -> log.getCompletionRate() != null && log.getCompletionRate() >= 80)
			.count();

		int exerciseCompletionRate = totalExercises > 0
			? (int) ((completedExercises * 100) / totalExercises)
			: 0;
		boolean allExercisesCompleted = completedExercises == totalExercises && totalExercises > 0;

		// 평균 통증 점수 계산
		double avgPainScore = logs.stream()
			.filter(log -> log.getPainAfter() != null)
			.mapToInt(ExerciseLog::getPainAfter)
			.average()
			.orElse(0.0);

		// 총 운동 시간 계산
		int totalDurationSec = logs.stream()
			.filter(log -> log.getDurationSec() != null)
			.mapToInt(ExerciseLog::getDurationSec)
			.sum();

		// 평균 RPE 계산
		double avgRpe = logs.stream()
			.filter(log -> log.getRpe() != null)
			.mapToInt(ExerciseLog::getRpe)
			.average()
			.orElse(0.0);

		// dailyMetrics 구성
		Map<String, Object> dailyMetrics = new HashMap<>();
		dailyMetrics.put("totalExercises", totalExercises);
		dailyMetrics.put("completedExercises", completedExercises);
		dailyMetrics.put("avgRpe", Math.round(avgRpe * 10) / 10.0);

		String dailyMetricsJson = convertToJson(dailyMetrics);

		// 일일 요약 조회 또는 생성
		DailySummary summary = dailySummaryRepository.findByUser_UserIdAndDate(userId, date)
			.orElseGet(() -> DailySummary.builder()
				.user(user)
				.date(date)
				.build());

		// 업데이트 (새로운 객체 생성)
		DailySummary updatedSummary = DailySummary.builder()
			.summaryId(summary.getSummaryId())
			.user(user)
			.date(date)
			.allExercisesCompleted(allExercisesCompleted)
			.exerciseCompletionRate(exerciseCompletionRate)
			.allMedicationsTaken(false) // 복약 정보는 추후 구현
			.medicationCompletionRate(0) // 복약 완료율은 추후 구현
			.avgPainScore((int) Math.round(avgPainScore))
			.totalDurationSec(totalDurationSec)
			.dailyMetrics(dailyMetricsJson)
			.build();

		dailySummaryRepository.save(updatedSummary);

		log.info("일일 요약 업데이트 완료 - summaryId: {}", updatedSummary.getSummaryId());
	}

	/**
	 * DailySummary -> DailySummaryResponse 변환
	 */
	private DailySummaryResponse convertToDailySummaryResponse(DailySummary summary) {
		return DailySummaryResponse.builder()
			.summaryId(summary.getSummaryId())
			.userId(summary.getUser().getUserId())
			.date(summary.getDate())
			.allExercisesCompleted(summary.getAllExercisesCompleted())
			.exerciseCompletionRate(summary.getExerciseCompletionRate())
			.allMedicationsTaken(summary.getAllMedicationsTaken())
			.medicationCompletionRate(summary.getMedicationCompletionRate())
			.avgPainScore(summary.getAvgPainScore())
			.totalDurationSec(summary.getTotalDurationSec())
			.dailyMetrics(parseJson(summary.getDailyMetrics()))
			.createdAt(summary.getCreatedAt())
			.updatedAt(summary.getUpdatedAt())
			.build();
	}

	/**
	 * JSON 문자열을 JsonNode로 변환
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

	/**
	 * Map을 JSON 문자열로 변환
	 */
	private String convertToJson(Map<String, Object> map) {
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			log.error("JSON 변환 실패: {}", map, e);
			return "{}";
		}
	}
}







