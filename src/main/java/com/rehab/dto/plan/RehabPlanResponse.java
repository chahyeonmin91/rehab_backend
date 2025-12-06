package com.rehab.dto.plan;

import com.rehab.domain.entity.enums.RehabPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 재활 플랜 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RehabPlanResponse {

	private Long rehabPlanId;
	private Long userId;
	private String title;
	private RehabPlanStatus status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}







