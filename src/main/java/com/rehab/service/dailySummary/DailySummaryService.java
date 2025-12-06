package com.rehab.service.dailySummary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.DailySummary;
import com.rehab.domain.entity.ExerciseLog;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.dailySummary.DailySummaryResponse;
import com.rehab.domain.repository.dailySummary.DailySummaryRepository;
import com.rehab.domain.repository.exercise.ExerciseLogRepository;
import com.rehab.domain.repository.plan.PlanItemRepository;
import com.rehab.service.streak.StreakService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	private final StreakService streakService;
	private final ObjectMapper objectMapper;

	/**
	 * 일일 요약 조회
	 *
	 * - 컨트롤러에서는 LocalDate(YYYY-MM-DD)로 받고
	 * - 여기서는 해당 날짜의 0시(LocalDateTime)로 변환해서 조회
	 */
	public DailySummaryResponse getDailySummary(Long userId, LocalDate date) {
		log.info("일일 요약 조회 - userId: {}, date: {}", userId, date);

		LocalDateTime startOfDay = date.atStartOfDay();

		// DailySummary.date 는 LocalDateTime(하루의 시작 시각)이라고 가정
		DailySummary summary = dailySummaryRepository
			.findByUser_UserIdAndDate(userId, startOfDay)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.DAILY_SUMMARY_NOT_FOUND));

		return convertToDailySummaryResponse(summary);
	}

	/**
	 * 일일 요약 업데이트 (운동 로그 생성 시 호출)
	 *
	 * - loggedAt(LocalDateTime)을 기준으로 해당 "하루" 범위를 계산해서 로그 집계
	 */
	@Transactional
	public void updateDailySummary(Long userId, LocalDateTime dateTime) {
		log.info("일일 요약 업데이트 - userId: {}, dateTime: {}", userId, dateTime);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		// 기준 날짜 (연속성/스트릭은 날짜 단위)
		LocalDate targetDate = dateTime.toLocalDate();
		LocalDateTime startOfDay = targetDate.atStartOfDay();
		LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

		// 해당 날짜 범위의 운동 로그 조회
		// 👉 ExerciseLogRepository 에 아래 메서드가 있어야 함:
		// List<ExerciseLog> findByUser_UserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
		List<ExerciseLog> logs = exerciseLogRepository
			.findByUser_UserIdAndLoggedAtBetween(userId, startOfDay, endOfDay);

		if (logs.isEmpty()) {
			log.warn("운동 로그가 없어 일일 요약을 업데이트하지 않습니다. userId: {}, date: {}", userId, targetDate);
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

		// 복약 완료율 (현재는 0, 추후 구현)
		int medicationCompletionRate = 0;

		// 일일 요약 조회 또는 생성
		// DailySummary.date = 해당 날짜의 00:00:00(LocalDateTime)
		DailySummary summary = dailySummaryRepository
			.findByUser_UserIdAndDate(userId, startOfDay)
			.orElseGet(() -> DailySummary.builder()
				.user(user)
				.date(startOfDay)
				.build()
			);

		// 업데이트 (새로운 객체 생성, 기존 summaryId 유지)
		DailySummary updatedSummary = DailySummary.builder()
			.summaryId(summary.getSummaryId())
			.user(user)
			.date(startOfDay)
			.allExercisesCompleted(allExercisesCompleted)
			.exerciseCompletionRate(exerciseCompletionRate)
			.allMedicationsTaken(false) // 복약 정보는 추후 구현
			.medicationCompletionRate(medicationCompletionRate)
			.avgPainScore((int) Math.round(avgPainScore))
			.totalDurationSec(totalDurationSec)
			.dailyMetrics(dailyMetricsJson)
			.build();

		dailySummaryRepository.save(updatedSummary);

		log.info("일일 요약 업데이트 완료 - summaryId: {}", updatedSummary.getSummaryId());

		// Streak 업데이트 (날짜 단위로 처리)
		try {
			streakService.updateStreakFromDailySummary(
				userId,
				targetDate,
				exerciseCompletionRate,
				medicationCompletionRate
			);
			log.info("Streak 업데이트 완료 - userId: {}, date: {}", userId, targetDate);
		} catch (Exception e) {
			// Streak 업데이트 실패해도 일일 요약은 저장
			log.error("Streak 업데이트 실패 - userId: {}, date: {}", userId, targetDate, e);
		}
	}

	/**
	 * DailySummary -> DailySummaryResponse 변환
	 */
	private DailySummaryResponse convertToDailySummaryResponse(DailySummary summary) {
		return DailySummaryResponse.builder()
			.summaryId(summary.getSummaryId())
			.userId(summary.getUser().getUserId())
			// DailySummary.date 가 LocalDateTime 이라면 toLocalDate()로 변환
			.date(summary.getDate().toLocalDate())
			.allExercisesCompleted(summary.getAllExercisesCompleted())
			.exerciseCompletionRate(summary.getExerciseCompletionRate())
			.allMedicationsTaken(summary.getAllMedicationsTaken())
			.medicationCompletionRate(summary.getMedicationCompletionRate())
			.avgPainScore(summary.getAvgPainScore())
			.totalDurationSec(summary.getTotalDurationSec())
			.dailyMetrics(parseJson(summary.getDailyMetrics().toString()))
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
