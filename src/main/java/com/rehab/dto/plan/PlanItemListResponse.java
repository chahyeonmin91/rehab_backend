package com.rehab.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 플랜 항목 목록 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanItemListResponse {

	private Long rehabPlanId;
	private LocalDate date;
	private List<PlanItemResponse> items;
}
