package com.rehab.service.rehabPlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.GeneralException;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.apiPayload.exception.handler.ExerciseHandler;
import com.rehab.apiPayload.exception.handler.RehabPlanHandler;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.*;
import com.rehab.domain.entity.enums.MealTime;
import com.rehab.domain.entity.enums.PlanPhase;
import com.rehab.domain.entity.enums.RehabPlanStatus;
import com.rehab.domain.repository.diet.DietPlanItemRepository;
import com.rehab.domain.repository.diet.DietRepository;
import com.rehab.domain.repository.exercise.ExerciseLogRepository;
import com.rehab.domain.repository.exercise.ExerciseRepository;
import com.rehab.domain.repository.medication.MedicationPlanItemRepository;
import com.rehab.domain.repository.medication.MedicationRepository;
import com.rehab.domain.repository.plan.PlanItemRepository;
import com.rehab.domain.repository.rehab.RehabPlanRepository;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.plan.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private final MedicationPlanItemRepository medicationPlanItemRepository;
	private final DietPlanItemRepository dietPlanItemRepository;
	private final ExerciseLogRepository exerciseLogRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final ExerciseRepository exerciseRepository;
	private final MedicationRepository  medicationRepository;
	private final DietRepository  dietRepository;


	/**
	 * 현재 활성 플랜 조회
	 */
	public RehabPlanResponse getCurrentPlan(Long userId) {
		log.info("현재 활성 플랜 조회 - userId: {}", userId);

		RehabPlan rehabPlan = rehabPlanRepository
			.findFirstByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, RehabPlanStatus.ACTIVE)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.NO_ACTIVE_PLAN));

		return convertToRehabPlanResponse(rehabPlan);
	}

	/**
	 * 플랜별 운동 항목만 조회 (기존 로직 유지)
	 */
	public PlanItemListResponse getPlanItems(Long rehabPlanId, LocalDate date, PlanPhase phase) {
		log.info("플랜 운동 항목 조회 - rehabPlanId: {}, date: {}, phase: {}", rehabPlanId, date, phase);

		// 플랜 존재 여부 확인
		RehabPlan rehabPlan = rehabPlanRepository.findById(rehabPlanId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.REHAB_PLAN_NOT_FOUND));

		// 운동 항목 조회
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

		List<PlanItemResponse> exerciseResponses = planItems.stream()
			.map(this::convertToPlanItemResponse)
			.collect(Collectors.toList());

		return PlanItemListResponse.builder()
			.rehabPlanId(rehabPlanId)
			.date(date)
			.exerciseItems(exerciseResponses)
			.build();
	}

	/**
	 * 플랜별 복약 항목 조회
	 */
	public List<MedicationPlanItemResponse> getMedicationPlanItems(Long rehabPlanId) {
		log.info("플랜 복약 항목 조회 - rehabPlanId: {}", rehabPlanId);

		// 플랜 존재 여부 확인
		if (!rehabPlanRepository.existsById(rehabPlanId)) {
			throw new RehabPlanException(ErrorStatus.REHAB_PLAN_NOT_FOUND);
		}

		List<MedicationPlanItem> medicationItems =
			medicationPlanItemRepository.findByRehabPlanIdWithMedication(rehabPlanId);

		return medicationItems.stream()
			.map(this::convertToMedicationPlanItemResponse)
			.collect(Collectors.toList());
	}

	/**
	 * 플랜별 식단 항목 조회
	 */
	public List<DietPlanItemResponse> getDietPlanItems(Long rehabPlanId, MealTime mealTime) {
		log.info("플랜 식단 항목 조회 - rehabPlanId: {}, mealTime: {}", rehabPlanId, mealTime);

		// 플랜 존재 여부 확인
		RehabPlan rehabPlan = rehabPlanRepository.findById(rehabPlanId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.REHAB_PLAN_NOT_FOUND));

		List<DietPlanItem> dietItems;
		if (mealTime != null) {
			dietItems = dietPlanItemRepository.findByRehabPlanAndMealTimeOrderByOrderIndex(
				rehabPlan, mealTime);
		} else {
			dietItems = dietPlanItemRepository.findByRehabPlanIdWithDiet(rehabPlanId);
		}

		return dietItems.stream()
			.map(this::convertToDietPlanItemResponse)
			.collect(Collectors.toList());
	}

	/**
	 * 플랜의 모든 항목 통합 조회 (운동 + 복약 + 식단)
	 */
	public AllPlanItemsResponse getAllPlanItems(Long rehabPlanId) {
		log.info("플랜 전체 항목 조회 - rehabPlanId: {}", rehabPlanId);

		// 플랜 조회
		RehabPlan rehabPlan = rehabPlanRepository.findById(rehabPlanId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.REHAB_PLAN_NOT_FOUND));

		// 운동 항목
		List<PlanItem> exerciseItems = planItemRepository.findByRehabPlanIdOrderByOrderIndex(rehabPlanId);
		List<PlanItemResponse> exercises = exerciseItems.stream()
			.map(this::convertToPlanItemResponse)
			.collect(Collectors.toList());

		// 복약 항목
		List<MedicationPlanItem> medicationItems =
			medicationPlanItemRepository.findByRehabPlanIdWithMedication(rehabPlanId);
		List<MedicationPlanItemResponse> medications = medicationItems.stream()
			.map(this::convertToMedicationPlanItemResponse)
			.collect(Collectors.toList());

		// 식단 항목
		List<DietPlanItem> dietItems = dietPlanItemRepository.findByRehabPlanIdWithDiet(rehabPlanId);
		List<DietPlanItemResponse> diets = dietItems.stream()
			.map(this::convertToDietPlanItemResponse)
			.collect(Collectors.toList());

		int totalCount = exercises.size() + medications.size() + diets.size();

		return AllPlanItemsResponse.builder()
			.rehabPlanId(rehabPlanId)
			.title(rehabPlan.getTitle())
			.exercises(exercises)
			.medications(medications)
			.diets(diets)
			.totalCount(totalCount)
			.build();
	}

	/**
	 * 모든 플랜 조회
	 */
	@Transactional(readOnly = true)
	public RehabPlanListResponse getAllPlans(Long userId, String status) {
		log.info("모든 플랜 조회 시작 - userId: {}, status: {}", userId, status);

		// 사용자 존재 확인
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// 플랜 조회 (상태 필터링)
		List<RehabPlan> plans;
		if (status != null && !status.isEmpty()) {
			try {
				// String을 Enum으로 변환
				RehabPlanStatus statusEnum = RehabPlanStatus.valueOf(status.toUpperCase());
				plans = rehabPlanRepository.findByUserAndStatusOrderByCreatedAtDesc(user, statusEnum);
			} catch (IllegalArgumentException e) {
				log.error("유효하지 않은 status 값: {}", status, e);
				throw new GeneralException(ErrorStatus.INVALID_STATUS);
			}
		} else {
			plans = rehabPlanRepository.findByUserOrderByCreatedAtDesc(user);
		}

		// DTO 변환
		List<RehabPlanListResponse.RehabPlanSummary> planSummaries = plans.stream()
			.map(plan -> {
				int exerciseCount = planItemRepository.countByRehabPlan(plan);
				int medicationCount = medicationPlanItemRepository.countByRehabPlan(plan);
				int dietCount = dietPlanItemRepository.countByRehabPlan(plan);
				int totalItems = exerciseCount + medicationCount + dietCount;

				return RehabPlanListResponse.RehabPlanSummary.builder()
					.rehabPlanId(plan.getRehabPlanId())
					.userId(plan.getUser().getUserId())
					.title(plan.getTitle())
					.status(plan.getStatus())
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
	 * MedicationPlanItem -> MedicationPlanItemResponse 변환
	 */
	private MedicationPlanItemResponse convertToMedicationPlanItemResponse(MedicationPlanItem item) {
		Medication medication = item.getMedication();

		return MedicationPlanItemResponse.builder()
			.medicationPlanItemId(item.getMedicationPlanItemId())
			.medicationId(medication.getMedicationId())
			.medicationName(medication.getName())
			.dose(medication.getDose())
			.status(item.getStatus())
			.orderIndex(item.getOrderIndex())
			.startDate(item.getStartDate())
			.endDate(item.getEndDate())
			.recommendationReason(parseJson(item.getRecommendationReason()))
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.build();
	}

	/**
	 * DietPlanItem -> DietPlanItemResponse 변환
	 */
	private DietPlanItemResponse convertToDietPlanItemResponse(DietPlanItem item) {
		Diet diet = item.getDiet();

		return DietPlanItemResponse.builder()
			.dietPlanItemId(item.getDietPlanItemId())
			.dietId(diet.getDietId())
			.dietTitle(diet.getTitle())
			.mealTime(item.getMealTime())
			.portion(item.getPortion())
			.status(item.getStatus())
			.orderIndex(item.getOrderIndex())
			.recommendationReason(parseJson(item.getRecommendationReason()))
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.build();
	}

	/**
	 * 재활 플랜 통합 생성 (운동 + 복약 + 식단)
	 */
	@Transactional
	public RehabPlanDetailResponse createRehabPlanWithItems(Long userId, CreateRehabPlanRequest request) {
		log.info("Creating rehab plan for user: {}, title: {}", userId, request.getTitle());

		// 1. 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

		// 2. RehabPlan 생성
		RehabPlan rehabPlan = RehabPlan.builder()
			.user(user)
			.title(request.getTitle())
			.status(RehabPlanStatus.ACTIVE)
			.startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
			.endDate(request.getEndDate())
			.meta(request.getMeta())
			.generatedBy(request.getGeneratedBy())
			.build();

		RehabPlan savedPlan = rehabPlanRepository.save(rehabPlan);
		log.info("RehabPlan created with ID: {}", savedPlan.getRehabPlanId());

		// 3. 운동 항목 생성
		List<PlanItem> planItems = createPlanItems(savedPlan, request.getExerciseItems());

		// 4. 복약 항목 생성
		List<MedicationPlanItem> medicationPlanItems = createMedicationPlanItems(savedPlan, request.getMedicationItems());

		// 5. 식단 항목 생성
		List<DietPlanItem> dietPlanItems = createDietPlanItems(savedPlan, request.getDietItems());

		// 6. 응답 생성
		return convertToRehabPlanDetailResponse(savedPlan, planItems, medicationPlanItems, dietPlanItems);
	}

	/**
	 * 운동 항목 생성
	 */
	private List<PlanItem> createPlanItems(RehabPlan rehabPlan, List<CreatePlanItemRequest> requests) {
		if (requests == null || requests.isEmpty()) {
			return List.of();
		}

		List<PlanItem> planItems = requests.stream()
			.map(req -> {
				Exercise exercise = exerciseRepository.findById(req.getExerciseId())
					.orElseThrow(() -> new ExerciseHandler(ErrorStatus.EXERCISE_NOT_FOUND));

				return PlanItem.builder()
					.rehabPlan(rehabPlan)
					.exercise(exercise)
					.phase(req.getPhase())
					.dose(req.getDose())
					.status(req.getStatus())
					.orderIndex(req.getOrderIndex())
					.recommendationReason(req.getRecommendationReason())
					.build();
			})
			.collect(Collectors.toList());

		return planItemRepository.saveAll(planItems);
	}

	/**
	 * 복약 항목 생성
	 */
	private List<MedicationPlanItem> createMedicationPlanItems(RehabPlan rehabPlan, List<CreateMedicationPlanItemRequest> requests) {
		if (requests == null || requests.isEmpty()) {
			return List.of();
		}

		List<MedicationPlanItem> medicationPlanItems = requests.stream()
			.map(req -> {
				Medication medication = medicationRepository.findById(req.getMedicationId())
					.orElseThrow(() -> new RehabPlanHandler(ErrorStatus.MEDICATION_NOT_FOUND));

				return MedicationPlanItem.builder()
					.rehabPlan(rehabPlan)
					.medication(medication)
					.status(req.getStatus())
					.orderIndex(req.getOrderIndex())
					.startDate(req.getStartDate())
					.endDate(req.getEndDate())
					.recommendationReason(req.getRecommendationReason())
					.build();
			})
			.collect(Collectors.toList());

		return medicationPlanItemRepository.saveAll(medicationPlanItems);
	}

	/**
	 * 식단 항목 생성
	 */
	private List<DietPlanItem> createDietPlanItems(RehabPlan rehabPlan, List<CreateDietPlanItemRequest> requests) {
		if (requests == null || requests.isEmpty()) {
			return List.of();
		}

		List<DietPlanItem> dietPlanItems = requests.stream()
			.map(req -> {
				Diet diet = dietRepository.findById(req.getDietId())
					.orElseThrow(() -> new RehabPlanHandler(ErrorStatus.DIET_NOT_FOUND));

				return DietPlanItem.builder()
					.rehabPlan(rehabPlan)
					.diet(diet)
					.mealTime(req.getMealTime())
					.portion(req.getPortion())
					.status(req.getStatus())
					.orderIndex(req.getOrderIndex())
					.recommendationReason(req.getRecommendationReason())
					.build();
			})
			.collect(Collectors.toList());

		return dietPlanItemRepository.saveAll(dietPlanItems);
	}

	/**
	 * RehabPlan -> RehabPlanDetailResponse 변환
	 */
	private RehabPlanDetailResponse convertToRehabPlanDetailResponse(
		RehabPlan plan,
		List<PlanItem> planItems,
		List<MedicationPlanItem> medicationPlanItems,
		List<DietPlanItem> dietPlanItems) {

		return RehabPlanDetailResponse.builder()
			.rehabPlanId(plan.getRehabPlanId())
			.userId(plan.getUser().getUserId())
			.title(plan.getTitle())
			.status(plan.getStatus())
			.startDate(plan.getStartDate())
			.endDate(plan.getEndDate())
			.meta(plan.getMeta())
			.generatedBy(plan.getGeneratedBy())
			.exerciseItems(planItems.stream()
				.map(this::convertToPlanItemResponse)
				.collect(Collectors.toList()))
			.medicationItems(medicationPlanItems.stream()
				.map(this::convertToMedicationPlanItemResponse)
				.collect(Collectors.toList()))
			.dietItems(dietPlanItems.stream()
				.map(this::convertToDietPlanItemResponse)
				.collect(Collectors.toList()))
			.totalItemCount(planItems.size() + medicationPlanItems.size() + dietPlanItems.size())
			.createdAt(plan.getCreatedAt())
			.updatedAt(plan.getUpdatedAt())
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
