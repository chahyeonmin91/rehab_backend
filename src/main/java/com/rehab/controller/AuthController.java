package com.rehab.controller;


import com.rehab.apiPayload.ApiResponse;
import com.rehab.common.util.CodeGenerator;
import com.rehab.dto.auth.AuthRequest;
import com.rehab.dto.auth.AuthResponse;
import com.rehab.dto.email.EmailRequest;
import com.rehab.service.authService.AuthService;
import com.rehab.service.emailService.EmailService;
import com.rehab.service.emailService.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입 / 로그인 API")
public class AuthController {

	private final CodeGenerator codeGenerator;
	private final EmailVerificationService emailVerificationService;
	private final EmailService emailService;


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
	@PostMapping("/auth/email/send")
	public ApiResponse<?> sendEmailCode(@RequestBody EmailRequest.Send request) {

		String code = codeGenerator.generate6DigitCode();
		emailVerificationService.saveVerificationCode(request.getEmail(), code);

		emailService.sendVerificationCode(request.getEmail(), code);

		return ApiResponse.onSuccess(null);
	}


	@PostMapping("/auth/email/verify")
	public ApiResponse<?> verifyEmailCode(@RequestBody EmailRequest.Verify request) {

		String saved = emailVerificationService.getVerificationCode(request.getEmail());

		if (saved == null || !saved.equals(request.getAuthCode())) {
			return ApiResponse.onFailure("400", "인증코드가 일치하지 않습니다.", null);
		}

		emailVerificationService.markVerified(request.getEmail());

		return ApiResponse.onSuccess(
			Map.of("verified", true)
		);
	}
}
