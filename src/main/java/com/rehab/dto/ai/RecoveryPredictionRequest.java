package com.rehab.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회복 예측 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회복 예측 요청")
public class RecoveryPredictionRequest {

	@NotNull(message = "현재 통증 수준은 필수입니다.")
	@Min(value = 1, message = "통증 수준은 1 이상이어야 합니다.")
	@Max(value = 10, message = "통증 수준은 10 이하여야 합니다.")
	@Schema(description = "현재 통증 수준 (1-10)", example = "6")
	private Integer currentPainLevel;

	@NotNull(message = "최근 순응도는 필수입니다.")
	@Min(value = 0, message = "순응도는 0 이상이어야 합니다.")
	@Max(value = 100, message = "순응도는 100 이하여야 합니다.")
	@Schema(description = "최근 순응도 (%)", example = "82")
	private Integer recentAdherence;

	@NotNull(message = "프로그램 경과 일수는 필수입니다.")
	@Min(value = 0, message = "경과 일수는 0 이상이어야 합니다.")
	@Schema(description = "프로그램 경과 일수", example = "14")
	private Integer daysInProgram;
}







