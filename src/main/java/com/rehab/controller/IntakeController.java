package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.User;
import com.rehab.dto.intake.IntakeDto;
import com.rehab.service.intakeService.IntakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me/intake")
@RequiredArgsConstructor
@Tag(name = "Intake", description = "문진 정보 API")
public class IntakeController {

	private final IntakeService intakeService;

	@PutMapping
	@Operation(summary = "문진 정보 저장/수정", description = "초기 문진 정보 저장 또는 수정")
	public ApiResponse<IntakeDto.IntakeResponse> saveOrUpdateIntake(
		@AuthenticationPrincipal User user,
		@RequestBody IntakeDto.IntakeRequest request
	) {
		return ApiResponse.onSuccess(intakeService.saveOrUpdateIntake(user, request));
	}

	@GetMapping
	@Operation(summary = "문진 정보 조회", description = "내 문진 정보 조회")
	public ApiResponse<IntakeDto.IntakeResponse> getMyIntake(
		@AuthenticationPrincipal User user
	) {
		return ApiResponse.onSuccess(intakeService.getMyIntake(user));
	}
}
