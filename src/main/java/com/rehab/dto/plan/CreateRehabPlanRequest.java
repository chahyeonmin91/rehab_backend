package com.rehab.dto.plan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "재활 플랜 생성 요청")
public class CreateRehabPlanRequest {

	@NotBlank(message = "플랜 제목은 필수입니다")
	@Schema(description = "플랜 제목", example = "허리 통증 4주 종합 재활 플랜")
	private String title;

	@Schema(description = "시작 날짜", example = "2025-12-01T00:00:00")
	private LocalDateTime startDate;

	@Schema(description = "종료 날짜", example = "2025-12-28T23:59:59")
	private LocalDateTime endDate;

	@Schema(description = "플랜 메타 정보 (JSON)", example = "{\"goal\":\"통증 감소 및 일상 복귀\",\"painLevel\":\"7\"}, \"targetArea\":\"LOWER_BACK\"")
	private String meta;

	@Schema(description = "생성 방식", example = "AI_RECOMMENDATION")
	private String generatedBy;

	@Valid
	@Builder.Default
	@Schema(description = "운동 항목 리스트")
	private List<CreatePlanItemRequest> exerciseItems = new ArrayList<>();

	@Valid
	@Builder.Default
	@Schema(description = "복약 항목 리스트")
	private List<CreateMedicationPlanItemRequest> medicationItems = new ArrayList<>();

	@Valid
	@Builder.Default
	@Schema(description = "식단 항목 리스트")
	private List<CreateDietPlanItemRequest> dietItems = new ArrayList<>();
}
