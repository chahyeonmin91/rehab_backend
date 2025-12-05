package com.rehab.controller;


import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.auth.AuthRequest;
import com.rehab.dto.auth.AuthResponse;
import com.rehab.service.authService.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입 / 로그인 API")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	@Operation(summary = "이메일 회원가입", description = "EMAIL 방식 회원가입")
	public ApiResponse<AuthResponse.SignupResponse> signup(
		@RequestBody AuthRequest.SignupRequest request
	) {
		return ApiResponse.onSuccess(authService.signup(request));
	}

	@PostMapping("/login")
	@Operation(summary = "이메일 로그인", description = "EMAIL 방식 로그인")
	public ApiResponse<AuthResponse.LoginResponse> login(
		@RequestBody AuthRequest.LoginRequest request
	) {
		return ApiResponse.onSuccess(authService.login(request));
	}

//	@PostMapping("/auth/email/send")
//	public ApiResponse<?> sendEmail(@RequestBody EmailRequest.Send request) {
//		return ApiResponse.onSuccess("이메일 인증코드 발송 예정 기능");
//	}
//
//	@PostMapping("/auth/email/verify")
//	public ApiResponse<?> verifyEmail(@RequestBody EmailRequest.Verify request) {
//		return ApiResponse.onSuccess("인증코드 검증 예정 기능");
//	}

}
