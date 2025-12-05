package com.rehab.dto.auth;

import lombok.*;

public class AuthRequest {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SignupRequest {
		private String email;
		private String password;
		private String passwordCheck;
		private Boolean emailVerified; // 일단 true로 오게 두고, 나중에 이메일 인증 연동
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class LoginRequest {
		private String email;
		private String password;
	}
}
