package com.rehab.service.dailySummary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.DailySummary;
import com.rehab.domain.entity.DietLog;
import com.rehab.domain.entity.ExerciseLog;
import com.rehab.domain.entity.MedicationLog;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.diet.DietLogRepository;
import com.rehab.domain.repository.diet.DietPlanItemRepository;
import com.rehab.domain.repository.medication.MedicationLogRepository;
import com.rehab.domain.repository.medication.MedicationPlanItemRepository;
import com.rehab.domain.repository.rehab.RehabPlanRepository;
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
	private final MedicationLogRepository medicationLogRepository;
	private final DietLogRepository dietLogRepository;
	private final PlanItemRepository planItemRepository;
	private final MedicationPlanItemRepository medicationPlanItemRepository;
	private final DietPlanItemRepository dietPlanItemRepository;
	private final RehabPlanRepository rehabPlanRepository;
	private final UserRepository userRepository;
	private final StreakService streakService;
	private final ObjectMapper objectMapper;

	/**
	 * 일일 요약 조회
	 */
	public DailySummaryResponse getDailySummary(Long userId, LocalDate date) {
		log.info("일일 요약 조회 - userId: {}, date: {}", userId, date);

		LocalDateTime startOfDay = date.atStartOfDay();

		DailySummary summary = dailySummaryRepository
			.findByUser_UserIdAndDate(userId, startOfDay)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.DAILY_SUMMARY_NOT_FOUND));

		return convertToDailySummaryResponse(summary);
	}

	/**
	 * 일일 요약 업데이트 (운동/복약/식단 로그 생성 시 호출)
	 */
	@Transactional
	public void updateDailySummary(Long userId, LocalDateTime dateTime) {
		log.info("일일 요약 업데이트 - userId: {}, dateTime: {}", userId, dateTime);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		// 기준 날짜
		LocalDate targetDate = dateTime.toLocalDate();
		LocalDateTime startOfDay = targetDate.atStartOfDay();
		LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

		// 현재 활성 플랜 조회
		RehabPlan activePlan = rehabPlanRepository
			.findFirstByUser_UserIdAndStatusOrderByCreatedAtDesc(userId,
				com.rehab.domain.entity.enums.RehabPlanStatus.ACTIVE)
			.orElse(null);

		if (activePlan == null) {
			log.warn("활성 플랜이 없어 일일 요약을 업데이트하지 않습니다. userId: {}, date: {}", userId, targetDate);
			return;
		}

		// 1. 운동 완료율 계산
		ExerciseCompletionResult exerciseResult = calculateExerciseCompletion(
			userId, activePlan.getRehabPlanId(), startOfDay, endOfDay);

		// 2. 복약 완료율 계산
		MedicationCompletionResult medicationResult = calculateMedicationCompletion(
			userId, activePlan.getRehabPlanId(), startOfDay, endOfDay);

		// 3. 식단 완료율 계산
		DietCompletionResult dietResult = calculateDietCompletion(
			userId, activePlan.getRehabPlanId(), startOfDay, endOfDay);

		// dailyMetrics 구성
		Map<String, Object> dailyMetrics = new HashMap<>();
		dailyMetrics.put("totalExercises", exerciseResult.totalCount);
		dailyMetrics.put("completedExercises", exerciseResult.completedCount);
		dailyMetrics.put("avgRpe", exerciseResult.avgRpe);
		dailyMetrics.put("totalMedications", medicationResult.totalCount);
		dailyMetrics.put("takenMedications", medicationResult.completedCount);
		dailyMetrics.put("totalDiets", dietResult.totalCount);
		dailyMetrics.put("completedDiets", dietResult.completedCount);

		String dailyMetricsJson = convertToJson(dailyMetrics);

		// 일일 요약 조회 또는 생성
		DailySummary summary = dailySummaryRepository
			.findByUser_UserIdAndDate(userId, startOfDay)
			.orElseGet(() -> DailySummary.builder()
				.user(user)
				.date(startOfDay)
				.build()
			);

		// 업데이트
		DailySummary updatedSummary = DailySummary.builder()
			.summaryId(summary.getSummaryId())
			.user(user)
			.date(startOfDay)
			// 운동
			.allExercisesCompleted(exerciseResult.allCompleted)
			.exerciseCompletionRate(exerciseResult.completionRate)
			// 복약
			.allMedicationsTaken(medicationResult.allCompleted)
			.medicationCompletionRate(medicationResult.completionRate)
			// 식단
			.allDietCompleted(dietResult.allCompleted)
			.dietCompletionRate(dietResult.completionRate)
			// 기타
			.avgPainScore(exerciseResult.avgPainScore)
			.totalDurationSec(exerciseResult.totalDurationSec)
			.dailyMetrics(dailyMetricsJson)
			.build();

		dailySummaryRepository.save(updatedSummary);

		log.info("일일 요약 업데이트 완료 - summaryId: {}, 운동: {}%, 복약: {}%, 식단: {}%",
			updatedSummary.getSummaryId(),
			exerciseResult.completionRate,
			medicationResult.completionRate,
			dietResult.completionRate);

		// Streak 업데이트
		try {
			streakService.updateStreakFromDailySummary(
				userId,
				targetDate,
				exerciseResult.completionRate,
				medicationResult.completionRate
			);
			log.info("Streak 업데이트 완료 - userId: {}, date: {}", userId, targetDate);
		} catch (Exception e) {
			log.error("Streak 업데이트 실패 - userId: {}, date: {}", userId, targetDate, e);
		}
	}

	/**
	 * 운동 완료율 계산
	 */
	private ExerciseCompletionResult calculateExerciseCompletion(
		Long userId, Long rehabPlanId, LocalDateTime startOfDay, LocalDateTime endOfDay) {

		// 해당 플랜의 총 운동 항목 수
		long totalExercises = planItemRepository.countByRehabPlan_RehabPlanId(rehabPlanId);

		// 해당 날짜의 운동 로그
		List<ExerciseLog> logs = exerciseLogRepository
			.findByUser_UserIdAndLoggedAtBetween(userId, startOfDay, endOfDay);

		if (totalExercises == 0) {
			return new ExerciseCompletionResult(0, 0, 0, true, 0, 0, 0.0);
		}

		// 완료된 운동 (완료율 80% 이상)
		long completedExercises = logs.stream()
			.filter(log -> log.getCompletionRate() != null && log.getCompletionRate() >= 80)
			.count();

		int completionRate = (int) ((completedExercises * 100) / totalExercises);
		boolean allCompleted = completedExercises >= totalExercises;

		// 평균 통증 점수
		int avgPainScore = (int) Math.round(logs.stream()
			.filter(log -> log.getPainAfter() != null)
			.mapToInt(ExerciseLog::getPainAfter)
			.average()
			.orElse(0.0));

		// 총 운동 시간
		int totalDurationSec = logs.stream()
			.filter(log -> log.getDurationSec() != null)
			.mapToInt(ExerciseLog::getDurationSec)
			.sum();

		// 평균 RPE
		double avgRpe = Math.round(logs.stream()
			.filter(log -> log.getRpe() != null)
			.mapToInt(ExerciseLog::getRpe)
			.average()
			.orElse(0.0) * 10) / 10.0;

		return new ExerciseCompletionResult(
			totalExercises, completedExercises, completionRate, allCompleted,
			avgPainScore, totalDurationSec, avgRpe);
	}

	/**
	 * 복약 완료율 계산
	 */
	private MedicationCompletionResult calculateMedicationCompletion(
		Long userId, Long rehabPlanId, LocalDateTime startOfDay, LocalDateTime endOfDay) {

		// 해당 플랜의 총 복약 항목 수
		long totalMedications = medicationPlanItemRepository.countByRehabPlan_RehabPlanId(rehabPlanId);

		if (totalMedications == 0) {
			return new MedicationCompletionResult(0, 0, 0, true);
		}

		// 해당 날짜의 복약 로그
		List<MedicationLog> logs = medicationLogRepository
			.findByUser_UserIdAndTakenAtBetween(userId, startOfDay, endOfDay);

		// 복용 완료된 복약 (taken = true)
		long takenMedications = logs.stream()
			.filter(MedicationLog::getTaken)
			.count();

		int completionRate = (int) ((takenMedications * 100) / totalMedications);
		boolean allCompleted = takenMedications >= totalMedications;

		return new MedicationCompletionResult(
			totalMedications, takenMedications, completionRate, allCompleted);
	}

	/**
	 * 식단 완료율 계산
	 */
	private DietCompletionResult calculateDietCompletion(
		Long userId, Long rehabPlanId, LocalDateTime startOfDay, LocalDateTime endOfDay) {

		// 해당 플랜의 총 식단 항목 수
		long totalDiets = dietPlanItemRepository.countByRehabPlan_RehabPlanId(rehabPlanId);

		if (totalDiets == 0) {
			return new DietCompletionResult(0, 0, 0, true);
		}

		// 해당 날짜의 식단 로그
		List<DietLog> logs = dietLogRepository
			.findByUser_UserIdAndLoggedAtBetween(userId, startOfDay, endOfDay);

		// 완료된 식단 (completed = true 또는 portionConsumed >= 80)
		long completedDiets = logs.stream()
			.filter(log -> {
				if (log.getCompleted() != null && log.getCompleted()) {
					return true;
				}
				return log.getPortionConsumed() != null && log.getPortionConsumed() >= 80;
			})
			.count();

		int completionRate = (int) ((completedDiets * 100) / totalDiets);
		boolean allCompleted = completedDiets >= totalDiets;

		return new DietCompletionResult(
			totalDiets, completedDiets, completionRate, allCompleted);
	}

	/**
	 * DailySummary -> DailySummaryResponse 변환
	 */
	private DailySummaryResponse convertToDailySummaryResponse(DailySummary summary) {
		return DailySummaryResponse.builder()
			.summaryId(summary.getSummaryId())
			.userId(summary.getUser().getUserId())
			.date(summary.getDate().toLocalDate())
			.allExercisesCompleted(summary.getAllExercisesCompleted())
			.exerciseCompletionRate(summary.getExerciseCompletionRate())
			.allMedicationsTaken(summary.getAllMedicationsTaken())
			.medicationCompletionRate(summary.getMedicationCompletionRate())
			.allDietCompleted(summary.getAllDietCompleted())
			.dietCompletionRate(summary.getDietCompletionRate())
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

	// ===== 내부 클래스 (결과 객체) =====

	private static class ExerciseCompletionResult {
		long totalCount;
		long completedCount;
		int completionRate;
		boolean allCompleted;
		int avgPainScore;
		int totalDurationSec;
		double avgRpe;

		ExerciseCompletionResult(long totalCount, long completedCount, int completionRate,
			boolean allCompleted, int avgPainScore, int totalDurationSec, double avgRpe) {
			this.totalCount = totalCount;
			this.completedCount = completedCount;
			this.completionRate = completionRate;
			this.allCompleted = allCompleted;
			this.avgPainScore = avgPainScore;
			this.totalDurationSec = totalDurationSec;
			this.avgRpe = avgRpe;
		}
	}

	private static class MedicationCompletionResult {
		long totalCount;
		long completedCount;
		int completionRate;
		boolean allCompleted;

		MedicationCompletionResult(long totalCount, long completedCount,
			int completionRate, boolean allCompleted) {
			this.totalCount = totalCount;
			this.completedCount = completedCount;
			this.completionRate = completionRate;
			this.allCompleted = allCompleted;
		}
	}

	private static class DietCompletionResult {
		long totalCount;
		long completedCount;
		int completionRate;
		boolean allCompleted;

		DietCompletionResult(long totalCount, long completedCount,
			int completionRate, boolean allCompleted) {
			this.totalCount = totalCount;
			this.completedCount = completedCount;
			this.completionRate = completionRate;
			this.allCompleted = allCompleted;
		}
	}
}
