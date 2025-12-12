package com.rehab.dto.medication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "복약 로그 목록 응답")
public class MedicationLogListResponse {

	@Schema(description = "조회 날짜", example = "2025-12-01T00:00:00")
	private LocalDateTime date;

	@Schema(description = "복약 로그 목록")
	private List<MedicationLogResponse> logs;
}
