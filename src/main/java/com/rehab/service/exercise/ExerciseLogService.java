package com.rehab.service.exercise;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.ExerciseLog;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.ExerciseLogStatus;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.exercise.CreateExerciseLogRequest;
import com.rehab.dto.exercise.ExerciseLogListResponse;
import com.rehab.dto.exercise.ExerciseLogResponse;
import com.rehab.domain.repository.exercise.ExerciseLogRepository;
import com.rehab.domain.repository.plan.PlanItemRepository;
import com.rehab.service.dailySummary.DailySummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 운동 로그 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseLogService {

	private final ExerciseLogRepository exerciseLogRepository;
	private final PlanItemRepository planItemRepository;
	private final UserRepository userRepository;
	private final DailySummaryService dailySummaryService;

	/**
	 * 운동 로그 생성
	 */
	@Transactional
	public ExerciseLogResponse createExerciseLog(Long userId, CreateExerciseLogRequest request) {
		log.info("운동 로그 생성 - userId: {}, planItemId: {}", userId, request.getPlanItemId());

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		// 플랜 항목 조회
		PlanItem planItem = planItemRepository.findById(request.getPlanItemId())
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.PLAN_ITEM_NOT_FOUND));

		// 운동 로그 생성
		ExerciseLog exerciseLog = ExerciseLog.builder()
			.user(user)
			.planItem(planItem)
			.loggedAt(request.getLoggedAt())
			.painBefore(request.getPainBefore())
			.painAfter(request.getPainAfter())
			.rpe(request.getRpe())
			.completionRate(request.getCompletionRate())
			.durationSec(request.getDurationSec())
			.notes(request.getNotes())
			.status(ExerciseLogStatus.COMPLETED)
			.build();

		ExerciseLog savedLog = exerciseLogRepository.save(exerciseLog);

		log.info("운동 로그 생성 완료 - exerciseLogId: {}", savedLog.getExerciseLogId());

		// 일일 요약 업데이트 (비동기적으로 처리, 실패해도 로그 생성은 성공)
		try {
			dailySummaryService.updateDailySummary(userId, request.getLoggedAt());
			log.info("일일 요약 업데이트 완료 - userId: {}, date: {}",
				userId, request.getLoggedAt().toLocalDate());
		} catch (Exception e) {
			log.error("일일 요약 업데이트 실패 - userId: {}, date: {}, error: {}",
				userId, request.getLoggedAt().toLocalDate(), e.getMessage(), e);
			// DailySummary 업데이트 실패해도 운동 로그 생성은 성공으로 처리
		}

		return convertToExerciseLogResponse(savedLog);
	}


	/**
	 * 특정 날짜 운동 로그 조회
	 */
	public ExerciseLogListResponse getExerciseLogsByDate(Long userId, LocalDate date) {
		log.info("운동 로그 조회 - userId: {}, date: {}", userId, date);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		List<ExerciseLog> logs = exerciseLogRepository
			.findByUser_UserIdAndLoggedAtBetween(userId, startOfDay, endOfDay);

		List<ExerciseLogResponse> logResponses = logs.stream()
			.map(this::convertToExerciseLogResponse)
			.collect(Collectors.toList());

		return ExerciseLogListResponse.builder()
			.date(date.atStartOfDay()) // 혹은 그냥 date 저장하고 싶으면 필드 타입을 LocalDate로
			.logs(logResponses)
			.build();
	}

	/**
	 * ExerciseLog -> ExerciseLogResponse 변환
	 */
	private ExerciseLogResponse convertToExerciseLogResponse(ExerciseLog log) {
		return ExerciseLogResponse.builder()
			.exerciseLogId(log.getExerciseLogId())
			.userId(log.getUser().getUserId())
			.planItemId(log.getPlanItem().getPlanItemId())
			.loggedAt(log.getLoggedAt())
			.painBefore(log.getPainBefore())
			.painAfter(log.getPainAfter())
			.rpe(log.getRpe())
			.completionRate(log.getCompletionRate())
			.durationSec(log.getDurationSec())
			.notes(log.getNotes())
			.status(log.getStatus())
			.createdAt(log.getCreatedAt())
			.updatedAt(log.getUpdatedAt())
			.build();
	}
}







