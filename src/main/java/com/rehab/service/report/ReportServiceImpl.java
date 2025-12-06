package com.rehab.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.GeneralException;
import com.rehab.domain.entity.DailySummary;
import com.rehab.domain.entity.RecoveryScore;
import com.rehab.domain.entity.ReportSnapshot;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.ReportPeriod;
import com.rehab.domain.repository.dailySummary.DailySummaryRepository;
import com.rehab.domain.repository.recovery.RecoveryScoreRepository;
import com.rehab.domain.repository.report.ReportSnapshotRepository;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.report.ProgressReportResponse;
import com.rehab.dto.report.ReportSnapshotListResponse;
import com.rehab.dto.report.WeeklyReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService{

	private final DailySummaryRepository dailySummaryRepository;
	private final RecoveryScoreRepository recoveryScoreRepository;
	private final ReportSnapshotRepository reportSnapshotRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 진행률 리포트 조회
	 */
	public ProgressReportResponse getProgressReport(Long userId, String range, LocalDateTime endDate) {
		log.info("Fetching progress report for userId: {}, range: {}, endDate: {}", userId, range, endDate);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		LocalDateTime end = (endDate != null) ? endDate : LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

		int days = parseDays(range);
		LocalDateTime start = end.minusDays(days - 1).with(LocalTime.MIN);

		List<DailySummary> summaries = dailySummaryRepository
			.findByUserIdAndDateBetween(userId, start, end);

		if (summaries.isEmpty()) {
			log.warn("No daily summaries found for userId: {} in range {} to {}", userId, start, end);
		}

		ProgressReportResponse.ExerciseStats exerciseStats = buildExerciseStats(summaries);
		ProgressReportResponse.MedicationStats medicationStats = buildMedicationStats(summaries);
		ProgressReportResponse.PainStats painStats = buildPainStats(summaries);

		return ProgressReportResponse.builder()
			.range(range)
			.startDate(start)
			.endDate(end)
			.exerciseStats(exerciseStats)
			.medicationStats(medicationStats)
			.painStats(painStats)
			.build();
	}

	/**
	 * 주간 하이라이트 조회
	 */
	@Transactional
	public WeeklyReportResponse getWeeklyReport(Long userId, LocalDate weekStart) {
		log.info("Fetching weekly report for userId: {}, weekStart: {}", userId, weekStart);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		LocalDate start = (weekStart != null) ? weekStart :
			LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate end = start.plusDays(6);

		String startStr = start.toString();
		String endStr = end.toString();
		Optional<ReportSnapshot> existingSnapshot = reportSnapshotRepository
			.findWeeklyReportByRange(userId, startStr, endStr);

		if (existingSnapshot.isPresent()) {
			log.info("Found existing weekly report for userId: {}, range: {} to {}", userId, start, end);
			return mapToWeeklyReportResponse(existingSnapshot.get());
		}

		log.info("Creating new weekly report for userId: {}, range: {} to {}", userId, start, end);
		ReportSnapshot newSnapshot = createWeeklySnapshot(user, start, end);
		reportSnapshotRepository.save(newSnapshot);

		return mapToWeeklyReportResponse(newSnapshot);
	}

	/**
	 * 리포트 스냅샷 목록 조회
	 */
	public ReportSnapshotListResponse getReportSnapshots(Long userId, String period, Integer limit) {
		log.info("Fetching report snapshots for userId: {}, period: {}, limit: {}", userId, period, limit);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		int pageSize = (limit != null && limit > 0) ? limit : 10;
		PageRequest pageRequest = PageRequest.of(0, pageSize);

		List<ReportSnapshot> snapshots;
		if (period != null && !period.isEmpty()) {
			ReportPeriod periodEnum = ReportPeriod.valueOf(period.toUpperCase());
			snapshots = reportSnapshotRepository
				.findByUser_UserIdAndPeriodOrderByGeneratedAtDesc(userId, periodEnum, pageRequest);
		} else {
			snapshots = reportSnapshotRepository
				.findByUser_UserIdOrderByGeneratedAtDesc(userId, pageRequest);
		}

		List<ReportSnapshotListResponse.ReportSnapshotItem> items = snapshots.stream()
			.map(this::mapToSnapshotItem)
			.collect(Collectors.toList());

		return ReportSnapshotListResponse.builder()
			.snapshots(items)
			.totalCount(items.size())
			.build();
	}

	private int parseDays(String range) {
		return switch (range.toLowerCase()) {
			case "7d" -> 7;
			case "14d" -> 14;
			case "30d" -> 30;
			default -> throw new GeneralException(ErrorStatus._BAD_REQUEST);
		};
	}

	private ProgressReportResponse.ExerciseStats buildExerciseStats(List<DailySummary> summaries) {
		double avgRate = summaries.stream()
			.filter(s -> s.getExerciseCompletionRate() != null)
			.mapToInt(DailySummary::getExerciseCompletionRate)
			.average()
			.orElse(0.0);

		long totalDuration = summaries.stream()
			.filter(s -> s.getTotalDurationSec() != null)
			.mapToInt(DailySummary::getTotalDurationSec)
			.sum();

		List<ProgressReportResponse.DailyExerciseData> dailyData = summaries.stream()
			.map(s -> ProgressReportResponse.DailyExerciseData.builder()
				.date(s.getDate())
				.completionRate(s.getExerciseCompletionRate() != null ? s.getExerciseCompletionRate() : 0)
				.durationSec(s.getTotalDurationSec() != null ? s.getTotalDurationSec() : 0)
				.build())
			.collect(Collectors.toList());

		return ProgressReportResponse.ExerciseStats.builder()
			.avgCompletionRate((int) Math.round(avgRate))
			.totalDurationSec(totalDuration)
			.dailyData(dailyData)
			.build();
	}

	private ProgressReportResponse.MedicationStats buildMedicationStats(List<DailySummary> summaries) {
		double avgRate = summaries.stream()
			.filter(s -> s.getMedicationCompletionRate() != null)
			.mapToInt(DailySummary::getMedicationCompletionRate)
			.average()
			.orElse(0.0);

		List<ProgressReportResponse.DailyMedicationData> dailyData = summaries.stream()
			.map(s -> ProgressReportResponse.DailyMedicationData.builder()
				.date(s.getDate())
				.completionRate(s.getMedicationCompletionRate() != null ? s.getMedicationCompletionRate() : 0)
				.build())
			.collect(Collectors.toList());

		return ProgressReportResponse.MedicationStats.builder()
			.avgCompletionRate((int) Math.round(avgRate))
			.dailyData(dailyData)
			.build();
	}

	private ProgressReportResponse.PainStats buildPainStats(List<DailySummary> summaries) {
		double avgPain = summaries.stream()
			.filter(s -> s.getAvgPainScore() != null)
			.mapToInt(DailySummary::getAvgPainScore)
			.average()
			.orElse(0.0);

		List<ProgressReportResponse.DailyPainData> dailyData = summaries.stream()
			.filter(s -> s.getAvgPainScore() != null)
			.map(s -> ProgressReportResponse.DailyPainData.builder()
				.date(s.getDate())
				.avgPain(s.getAvgPainScore())
				.build())
			.collect(Collectors.toList());

		return ProgressReportResponse.PainStats.builder()
			.avgPainScore((int) Math.round(avgPain))
			.dailyData(dailyData)
			.build();
	}

	private ReportSnapshot createWeeklySnapshot(User user, LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
		List<DailySummary> summaries = dailySummaryRepository
			.findByUserIdAndDateBetween(user.getUserId(), startDateTime, endDateTime);

		String weeklyHighlightText = generateWeeklyHighlight(summaries);
		String weeklyHighlight = toJson(weeklyHighlightText);  // JSON 문자열로 변환
		String metrics = generateMetrics(summaries);
		BigDecimal recoveryPrediction = getRecoveryPrediction(user.getUserId(), end);

		Map<String, String> rangeMap = new HashMap<>();
		rangeMap.put("start", start.toString());
		rangeMap.put("end", end.toString());
		String coveredRange = toJson(rangeMap);

		return ReportSnapshot.builder()
			.user(user)
			.period(ReportPeriod.WEEKLY)
			.coveredRange(coveredRange)
			.weeklyHighlight(weeklyHighlight)
			.metrics(metrics)
			.recoveryPrediction(recoveryPrediction)
			.generatedAt(LocalDateTime.now())
			.build();
	}

	private String generateWeeklyHighlight(List<DailySummary> summaries) {
		if (summaries.isEmpty()) {
			return "이번 주에는 기록이 없어요. 다음 주부터 열심히 해봐요!";
		}

		long activeDays = summaries.stream()
			.filter(DailySummary::meetsStreakCriteria)
			.count();

		double avgExerciseRate = summaries.stream()
			.filter(s -> s.getExerciseCompletionRate() != null)
			.mapToInt(DailySummary::getExerciseCompletionRate)
			.average()
			.orElse(0.0);

		if (activeDays == 7) {
			return "7일 연속 운동 달성! 꾸준한 습관이 회복을 만듭니다.";
		} else if (activeDays >= 5) {
			return String.format("이번 주 %d일 운동 완료! 거의 다 왔어요.", activeDays);
		} else if (avgExerciseRate >= 80) {
			return "높은 완료율을 유지하고 있어요. 계속 이대로만 가세요!";
		} else if (avgExerciseRate >= 60) {
			return "좋은 진행이에요. 조금만 더 꾸준히 해봐요!";
		} else {
			return "다음 주에는 조금 더 열심히 해봐요. 화이팅!";
		}
	}

	private String generateMetrics(List<DailySummary> summaries) {
		long totalExercises = summaries.size();

		double avgCompletionRate = summaries.stream()
			.filter(s -> s.getExerciseCompletionRate() != null)
			.mapToInt(DailySummary::getExerciseCompletionRate)
			.average()
			.orElse(0.0);

		Map<String, Object> metricsMap = new HashMap<>();
		metricsMap.put("totalExercises", totalExercises);
		metricsMap.put("avgCompletionRate", (int) Math.round(avgCompletionRate));

		return toJson(metricsMap);
	}

	private BigDecimal getRecoveryPrediction(Long userId, LocalDate date) {
		Optional<RecoveryScore> recoveryScore = recoveryScoreRepository
			.findByUser_UserIdAndDate(userId, date);

		return recoveryScore.map(RecoveryScore::getDailyScore)
			.orElse(BigDecimal.ZERO);
	}

	private String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize object to JSON", e);
			return "{}";
		}
	}

	private Map<String, String> fromJson(String json) {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse JSON string", e);
			return new HashMap<>();
		}
	}

	private String parseJsonString(String json) {
		if (json == null) {
			return null;
		}
		try {
			// JSON 문자열을 파�ing하여 순수 문자열로 변환
			// 예: "\"7일 연속...\"" -> "7일 연속..."
			return objectMapper.readValue(json, String.class);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse JSON string to plain text", e);
			return json; // 파싱 실패 시 원본 반환
		}
	}

	private WeeklyReportResponse mapToWeeklyReportResponse(ReportSnapshot snapshot) {
		Map<String, String> rangeMap = fromJson(snapshot.getCoveredRange());

		WeeklyReportResponse.DateRangeDto dateRange = WeeklyReportResponse.DateRangeDto.builder()
			.start(rangeMap.getOrDefault("start", ""))
			.end(rangeMap.getOrDefault("end", ""))
			.build();

		// JSON 문자열을 파싱하여 순수 문자열로 변환
		String weeklyHighlight = parseJsonString(snapshot.getWeeklyHighlight());

		return WeeklyReportResponse.builder()
			.reportSnapshotId(snapshot.getReportSnapshotId())
			.userId(snapshot.getUser().getUserId())
			.period(snapshot.getPeriod().name())
			.coveredRange(dateRange)
			.weeklyHighlight(weeklyHighlight)
			.metrics(snapshot.getMetrics())
			.recoveryPrediction(snapshot.getRecoveryPrediction())
			.generatedAt(snapshot.getGeneratedAt())
			.createdAt(snapshot.getCreatedAt())
			.updatedAt(snapshot.getUpdatedAt())
			.build();
	}

	private ReportSnapshotListResponse.ReportSnapshotItem mapToSnapshotItem(ReportSnapshot snapshot) {
		Map<String, String> rangeMap = fromJson(snapshot.getCoveredRange());

		ReportSnapshotListResponse.DateRangeDto dateRange = ReportSnapshotListResponse.DateRangeDto.builder()
			.start(rangeMap.getOrDefault("start", ""))
			.end(rangeMap.getOrDefault("end", ""))
			.build();

		// JSON 문자열을 파싱하여 순수 문자열로 변환
		String weeklyHighlight = parseJsonString(snapshot.getWeeklyHighlight());

		return ReportSnapshotListResponse.ReportSnapshotItem.builder()
			.reportSnapshotId(snapshot.getReportSnapshotId())
			.period(snapshot.getPeriod().name())
			.coveredRange(dateRange)
			.weeklyHighlight(weeklyHighlight)
			.metrics(snapshot.getMetrics())
			.recoveryPrediction(snapshot.getRecoveryPrediction())
			.generatedAt(snapshot.getGeneratedAt())
			.build();
	}
}
