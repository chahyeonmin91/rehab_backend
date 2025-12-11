package com.rehab.dto.medication;

import com.rehab.domain.entity.enums.TimeOfDay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 복약 로그 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "복약 로그 생성 요청")
public class CreateMedicationLogRequest {

	@NotNull(message = "복약 ID는 필수입니다")
	@Schema(description = "복약 ID", example = "1")
	private Long medicationId;

	@NotNull(message = "복약 시간은 필수입니다")
	@Schema(description = "복약 시간", example = "2025-12-11T08:00:00")
	private LocalDateTime takenAt;

	@NotNull(message = "복약 시간대는 필수입니다")
	@Schema(description = "복약 시간대", example = "MORNING", allowableValues = {"MORNING", "LUNCH", "DINNER", "BEFORE_BED"})
	private TimeOfDay timeOfDay;

	@NotNull(message = "복약 여부는 필수입니다")
	@Schema(description = "복약 완료 여부", example = "true")
	private Boolean taken;

	@Schema(description = "메모", example = "식후 30분에 복용")
	private String notes;
}
