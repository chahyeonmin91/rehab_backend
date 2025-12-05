package com.rehab.service.intakeService;

import com.rehab.domain.entity.User;
import com.rehab.dto.intake.IntakeDto;

public interface IntakeService {

	IntakeDto.IntakeResponse saveOrUpdateIntake(User user, IntakeDto.IntakeRequest request);

	IntakeDto.IntakeResponse getMyIntake(User user);
}

