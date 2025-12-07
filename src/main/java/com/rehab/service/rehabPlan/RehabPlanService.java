package com.rehab.service.rehabPlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.GeneralException;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.ExerciseLog;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.entity.RehabPlan;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.PlanPhase;
import com.rehab.domain.entity.enums.RehabPlanStatus;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.plan.PlanItemListResponse;
import com.rehab.dto.plan.PlanItemResponse;
import com.rehab.dto.plan.RehabPlanListResponse;
import com.rehab.dto.plan.RehabPlanResponse;
import com.rehab.domain.repository.exercise.ExerciseLogRepository;
import com.rehab.domain.repository.plan.PlanItemRepository;
import com.rehab.domain.repository.rehab.RehabPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 재활 플랜 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RehabPlanService {

	private final RehabPlanRepository rehabPlanRepository;
	private final PlanItemRepository planItemRepository;
	private final ExerciseLogRepository exerciseLogRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 현재 활성 플랜 조회
	 */
	public RehabPlanResponse getCurrentPlan(Long userId) {
		log.info("현재 활성 플랜 조회 - userId: {}", userId);

		RehabPlan rehabPlan = rehabPlanRepository
			.findCurrentPlanByUserIdAndStatus(userId, RehabPlanStatus.ACTIVE)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.NO_ACTIVE_PLAN));

		return convertToRehabPlanResponse(rehabPlan);
	}

	/**
	 * 플랜별 운동 항목 조회
	 */
	public PlanItemListResponse getPlanItems(Long rehabPlanId, LocalDate date, PlanPhase phase) {
		log.info("플랜 항목 조회 - rehabPlanId: {}, date: {}, phase: {}", rehabPlanId, date, phase);

		// 플랜 존재 여부 확인
		RehabPlan rehabPlan = rehabPlanRepository.findById(rehabPlanId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.REHAB_PLAN_NOT_FOUND));

		// 플랜 항목 조회
		List<PlanItem> planItems;
		if (phase != null) {
			planItems = planItemRepository.findByRehabPlanIdAndPhase(rehabPlanId, phase);
		} else {
			planItems = planItemRepository.findByRehabPlanIdOrderByOrderIndex(rehabPlanId);
		}

		// 특정 날짜가 지정된 경우, 해당 날짜의 완료 여부를 함께 조회
		Set<Long> completedPlanItemIds = Set.of();
		if (date != null) {
			List<ExerciseLog> logs = exerciseLogRepository.findByUserIdAndDate(
				rehabPlan.getUser().getUserId(), date.atStartOfDay());
			completedPlanItemIds = logs.stream()
				.map(log -> log.getPlanItem().getPlanItemId())
				.collect(Collectors.toSet());
		}

		List<PlanItemResponse> itemResponses = planItems.stream()
			.map(this::convertToPlanItemResponse)
			.collect(Collectors.toList());

		return PlanItemListResponse.builder()
			.rehabPlanId(rehabPlanId)
			.date(date)
			.items(itemResponses)
			.build();
	}

	@Transactional(readOnly = true)
	public RehabPlanListResponse getAllPlans(Long userId, String status) {
		log.info("모든 플랜 조회 시작 - userId: {}, status: {}", userId, status);

		// 사용자 존재 확인
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// 플랜 조회 (상태 필터링)
		List<RehabPlan> plans;
		if (status != null && !status.isEmpty()) {
			plans = rehabPlanRepository.findByUserAndStatus(user, status);
		} else {
			plans = rehabPlanRepository.findByUserOrderByCreatedAtDesc(user);
		}

		// DTO 변환
		List<RehabPlanListResponse.RehabPlanSummary> planSummaries = plans.stream()
			.map(plan -> {
				int totalItems = planItemRepository.countByRehabPlan(plan);
				return RehabPlanListResponse.RehabPlanSummary.builder()
					.rehabPlanId(plan.getRehabPlanId())
					.userId(plan.getUser().getUserId())
					.title(plan.getTitle())
					.status(plan.getStatus())  // 상태 결정 로직
					.totalItems(totalItems)
					.createdAt(plan.getCreatedAt().toString())
					.updatedAt(plan.getUpdatedAt().toString())
					.build();
			})
			.toList();

		return RehabPlanListResponse.builder()
			.plans(planSummaries)
			.totalCount(planSummaries.size())
			.build();
	}

	/**
	 * RehabPlan -> RehabPlanResponse 변환
	 */
	private RehabPlanResponse convertToRehabPlanResponse(RehabPlan rehabPlan) {
		return RehabPlanResponse.builder()
			.rehabPlanId(rehabPlan.getRehabPlanId())
			.userId(rehabPlan.getUser().getUserId())
			.title(rehabPlan.getTitle())
			.status(rehabPlan.getStatus())
			.startDate(rehabPlan.getStartDate())
			.endDate(rehabPlan.getEndDate())
			.createdAt(rehabPlan.getCreatedAt())
			.updatedAt(rehabPlan.getUpdatedAt())
			.build();
	}

	/**
	 * PlanItem -> PlanItemResponse 변환
	 */
	private PlanItemResponse convertToPlanItemResponse(PlanItem planItem) {
		return PlanItemResponse.builder()
			.planItemId(planItem.getPlanItemId())
			.exerciseId(planItem.getExercise().getExerciseId())
			.phase(planItem.getPhase())
			.orderIndex(planItem.getOrderIndex())
			.status(planItem.getStatus())
			.dose(parseJson(planItem.getDose()))
			.recommendationReason(parseJson(planItem.getRecommendationReason()))
			.createdAt(planItem.getCreatedAt())
			.updatedAt(planItem.getUpdatedAt())
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
}
