package com.rehab.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.rehab.domain.entity.enums.RehabPlanStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RehabPlanListResponse {
	private List<RehabPlanSummary> plans;
	private Integer totalCount;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RehabPlanSummary {
		private Long rehabPlanId;
		private Long userId;
		private String title;
		private RehabPlanStatus status;
		private Integer totalItems;  // 전체 운동 항목 수
		private String createdAt;
		private String updatedAt;
	}
}
