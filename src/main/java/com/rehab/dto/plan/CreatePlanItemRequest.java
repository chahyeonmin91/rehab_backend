package com.rehab.dto.plan;

import com.rehab.domain.entity.enums.PlanItemStatus;
import com.rehab.domain.entity.enums.RehabPhase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "운동 항목 생성 요청")
public class CreatePlanItemRequest {

	@NotNull(message = "운동 ID는 필수입니다")
	@Schema(description = "운동 ID", example = "1")
	private Long exerciseId;

	@Schema(description = "재활 단계", example = "ACUTE", allowableValues = {"ACUTE", "SUBACUTE", "CHRONIC", "MAINTENANCE"})
	private RehabPhase phase;

	@Schema(description = "운동 처방 정보 (JSON)", example = "{\"sets\":3,\"reps\":10,\"restSeconds\":30,\"holdSeconds\":5}")
	private String dose;

	@Builder.Default
	@Schema(description = "상태", example = "ACTIVE", allowableValues = {"ACTIVE", "PAUSED", "COMPLETED", "SKIPPED"})
	private PlanItemStatus status = PlanItemStatus.ACTIVE;

	@Schema(description = "순서", example = "1")
	private Integer orderIndex;

	@Schema(description = "추천 이유 (JSON)", example = "{\"mainReason\":\"허리 근력 강화에 효과적\",\"evidenceLevel\":\"HIGH\"}")
	private String recommendationReason;
}
