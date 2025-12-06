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

@Service
@RequiredArgsConstructor
@Transactional
public class IntakeServiceImpl implements IntakeService {

	private final SymptomIntakeRepository intakeRepository;

	@Override
	public IntakeDto.IntakeResponse saveOrUpdateIntake(User user, IntakeDto.IntakeRequest request) {

		SymptomIntake intake = intakeRepository.findByUser(user)
			.orElseGet(() -> SymptomIntake.builder()
				.user(user)
				.build()
			);

		intake.updateFromRequest(request); // 아래에 업데이트용 메서드 하나 추가하면 좋음
		SymptomIntake saved = intakeRepository.save(intake);

		return toResponse(saved);
	}

	@Override
	public IntakeDto.IntakeResponse getMyIntake(User user) {
		SymptomIntake intake = intakeRepository.findByUser(user)
			.orElseThrow(() -> new UserHandler(ErrorStatus._BAD_REQUEST)); // "문진 정보 없음" 에러코드 만들어도 좋음

		return toResponse(intake);
	}

	private IntakeDto.IntakeResponse toResponse(SymptomIntake intake) {
		return IntakeDto.IntakeResponse.builder()
			.intakeId(intake.getIntakeId())
			.painArea(intake.getPainArea())
			.painLevel(intake.getPainLevel())
			.goal(intake.getGoal())
			.exerciseExperience(intake.getExerciseExperience())
			.createdAt(intake.getCreatedAt())
			.updatedAt(intake.getUpdatedAt())
			.build();
	}
}
