package com.rehab.dto.medication;

import com.rehab.domain.entity.enums.TimeOfDay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 복약 로그 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "복약 로그 응답")
public class MedicationLogResponse {

	@Schema(description = "복약 로그 ID", example = "1")
	private Long medicationLogId;

	@Schema(description = "사용자 ID", example = "1")
	private Long userId;

	@Schema(description = "복약 ID", example = "1")
	private Long medicationId;

	@Schema(description = "복약 이름", example = "타이레놀")
	private String medicationName;

	@Schema(description = "복약 시간")
	private LocalDateTime takenAt;

	@Schema(description = "복약 시간대", example = "MORNING")
	private TimeOfDay timeOfDay;

	@Schema(description = "복약 완료 여부", example = "true")
	private Boolean taken;

	@Schema(description = "메모", example = "식후 30분에 복용")
	private String notes;

	@Schema(description = "생성일시")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시")
	private LocalDateTime updatedAt;
}
