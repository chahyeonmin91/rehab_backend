package com.rehab.dto.auth;

import com.rehab.domain.entity.enums.LoginType;
import lombok.*;

import java.time.LocalDateTime;

public class AuthResponse {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SignupResponse {
		private Long userId;
		private String email;
		private LoginType loginType;
		private String accessToken;
		private String refreshToken;
		private LocalDateTime createdAt;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class LoginResponse {
		private Long userId;
		private String email;
		private String username;
		private LoginType loginType;
		private String accessToken;
		private String refreshToken;
	}
}
