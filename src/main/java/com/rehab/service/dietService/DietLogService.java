package com.rehab.service.dietService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.DietLog;
import com.rehab.domain.entity.DietPlanItem;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.diet.DietLogRepository;
import com.rehab.domain.repository.diet.DietPlanItemRepository;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.diet.CreateDietLogRequest;
import com.rehab.dto.diet.DietLogResponse;
import com.rehab.service.dailySummary.DailySummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 식단 로그 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietLogService {

	private final DietLogRepository dietLogRepository;
	private final DietPlanItemRepository dietPlanItemRepository;
	private final UserRepository userRepository;
	private final DailySummaryService dailySummaryService;

	/**
	 * 식단 로그 생성
	 */
	@Transactional
	public DietLogResponse createDietLog(Long userId, CreateDietLogRequest request) {
		log.info("식단 로그 생성 - userId: {}, dietPlanItemId: {}", userId, request.getDietPlanItemId());

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		// 식단 플랜 항목 조회
		DietPlanItem dietPlanItem = dietPlanItemRepository.findById(request.getDietPlanItemId())
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.DIET_PLAN_ITEM_NOT_FOUND));

		// 식단 로그 생성
		DietLog dietLog = DietLog.builder()
			.user(user)
			.dietPlanItem(dietPlanItem)
			.loggedAt(request.getLoggedAt())
			.completed(request.getCompleted())
			.portionConsumed(request.getPortionConsumed())
			.notes(request.getNotes())
			.build();

		DietLog savedLog = dietLogRepository.save(dietLog);

		log.info("식단 로그 생성 완료 - dietLogId: {}", savedLog.getDietLogId());

		// ✅ 일일 요약 업데이트
		try {
			dailySummaryService.updateDailySummary(userId, request.getLoggedAt());
			log.info("일일 요약 업데이트 완료 - userId: {}, date: {}",
				userId, request.getLoggedAt().toLocalDate());
		} catch (Exception e) {
			log.error("일일 요약 업데이트 실패 - userId: {}, date: {}, error: {}",
				userId, request.getLoggedAt().toLocalDate(), e.getMessage(), e);
			// DailySummary 업데이트 실패해도 식단 로그 생성은 성공으로 처리
		}

		return convertToDietLogResponse(savedLog);
	}

	/**
	 * 특정 날짜 식단 로그 조회
	 */
	public List<DietLogResponse> getDietLogsByDateRange(
		Long userId, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("식단 로그 조회 - userId: {}, start: {}, end: {}", userId, startDate, endDate);

		List<DietLog> logs = dietLogRepository
			.findByUser_UserIdAndLoggedAtBetween(userId, startDate, endDate);

		return logs.stream()
			.map(this::convertToDietLogResponse)
			.collect(Collectors.toList());
	}

	/**
	 * DietLog -> DietLogResponse 변환
	 */
	private DietLogResponse convertToDietLogResponse(DietLog log) {
		return DietLogResponse.builder()
			.dietLogId(log.getDietLogId())
			.userId(log.getUser().getUserId())
			.dietPlanItemId(log.getDietPlanItem().getDietPlanItemId())
			.dietTitle(log.getDietPlanItem().getDiet().getTitle())
			.loggedAt(log.getLoggedAt())
			.completed(log.getCompleted())
			.portionConsumed(log.getPortionConsumed())
			.notes(log.getNotes())
			.createdAt(log.getCreatedAt())
			.updatedAt(log.getUpdatedAt())
			.build();
	}
}
