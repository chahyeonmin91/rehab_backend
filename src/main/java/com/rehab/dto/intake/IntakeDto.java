package com.rehab.dto.intake;

import com.rehab.domain.entity.enums.ExerciseExperience;
import com.rehab.domain.entity.enums.PainArea;
import lombok.*;

import java.time.LocalDateTime;

public class IntakeDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class IntakeRequest {
		private PainArea painArea;
		private Integer painLevel;
		private String goal;
		private ExerciseExperience exerciseExperience;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class IntakeResponse {
		private Long intakeId;
		private PainArea painArea;
		private Integer painLevel;
		private String goal;
		private ExerciseExperience exerciseExperience;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}
}

