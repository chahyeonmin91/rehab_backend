package com.rehab.service.intakeService;


import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.SymptomIntake;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.symptomIntake.SymptomIntakeRepository;
import com.rehab.dto.intake.IntakeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IntakeServiceImpl implements IntakeService {

	private final SymptomIntakeRepository symptomIntakeRepository;

	/**
	 * 문진 정보 저장 (1:N 구조 → 매번 새 이력 추가)
	 * 명세서 2.1 - PUT /users/me/intake
	 */
	@Override
	public IntakeDto.IntakeResponse saveOrUpdateIntake(User user, IntakeDto.IntakeRequest request) {
		if (user == null) {
			throw new UserHandler(ErrorStatus.MEMBER_NOT_FOUND);
		}

		SymptomIntake intake = SymptomIntake.builder()
			.user(user)
			.painArea(request.getPainArea())
			.painLevel(request.getPainLevel())
			.goal(request.getGoal())
			.exerciseExperience(request.getExerciseExperience())
			.build();

		SymptomIntake saved = symptomIntakeRepository.save(intake);

		return toResponse(saved);
	}

	/**
	 * 가장 최근 문진 1개 조회 (필요하면 사용)
	 */
	@Override
	@Transactional(readOnly = true)
	public IntakeDto.IntakeResponse getMyIntake(User user) {
		if (user == null) {
			throw new UserHandler(ErrorStatus.MEMBER_NOT_FOUND);
		}

		return symptomIntakeRepository.findAllByUserOrderByCreatedAtDesc(user)
			.stream()
			.findFirst()
			.map(this::toResponse)
			.orElse(null);  // 없으면 null 리턴 (프론트에서 분기)
	}


	/**
	 * 내 문진 이력 전체 조회 (최신순)
	 * 명세서: "문진 정보 조회 result 배열"
	 */
	@Override
	@Transactional(readOnly = true)
	public IntakeDto.IntakeListResponse getMyIntakes(User user) {
		if (user == null) {
			throw new UserHandler(ErrorStatus.MEMBER_NOT_FOUND);
		}

		List<SymptomIntake> intakes =
			symptomIntakeRepository.findAllByUserOrderByCreatedAtDesc(user);

		List<IntakeDto.IntakeResponse> responses = intakes.stream()
			.map(this::toResponse)
			.toList();

		return IntakeDto.IntakeListResponse.builder()
			.intakes(responses)
			.build();
	}

	/**
	 * 엔티티 → DTO 변환
	 */
	private IntakeDto.IntakeResponse toResponse(SymptomIntake intake) {
		return IntakeDto.IntakeResponse.builder()
			.intakeId(intake.getIntakeId())
			.userId(intake.getUser().getUserId())
			.painArea(intake.getPainArea())
			.painLevel(intake.getPainLevel())
			.goal(intake.getGoal())
			.exerciseExperience(intake.getExerciseExperience())
			.createdAt(intake.getCreatedAt())
			.updatedAt(intake.getUpdatedAt())
			.build();
	}
}
