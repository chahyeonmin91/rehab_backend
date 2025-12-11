package com.rehab.controller;


import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.User;
import com.rehab.dto.user.UserProfileDto;
import com.rehab.service.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 프로필 API")
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회", description = "JWT 기반 현재 로그인한 유저의 프로필 조회")
	public ApiResponse<UserProfileDto.ProfileResponse> getMyProfile(
		@AuthenticationPrincipal User user
	) {
		return ApiResponse.onSuccess(userService.getMyProfile(user));
	}

	@PostMapping("/me")
	@Operation(summary = "프로필 최초 생성", description = "회원가입 직후 프로필 최초 생성용 API")
	public ApiResponse<UserProfileDto.ProfileResponse> createMyProfile(
		@AuthenticationPrincipal User user,
		@RequestBody UserProfileDto.ProfileUpdateRequest request
	) {
		return ApiResponse.onSuccess(userService.updateMyProfile(user, request));
	}

	@PatchMapping("/me")
	@Operation(summary = "내 프로필 수정", description = "이름/성별/나이/키/몸무게 수정")
	public ApiResponse<UserProfileDto.ProfileResponse> updateMyProfile(
		@AuthenticationPrincipal User user,
		@RequestBody UserProfileDto.ProfileUpdateRequest request
	) {
		return ApiResponse.onSuccess(userService.updateMyProfile(user, request));
	}
}
