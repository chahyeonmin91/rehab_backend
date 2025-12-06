package com.rehab.dto.user;

import com.rehab.domain.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

public class UserProfileDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProfileResponse {
		private Long userId;
		private String username;
		private String email;
		private Gender gender;
		private Integer age;
		private Double height;
		private Double weight;
		private Boolean profileCompleted;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProfileUpdateRequest {
		private String username;
		private Gender gender;
		private Integer age;
		private Double height;
		private Double weight;

		private LocalDate birthDate;

	}
}

