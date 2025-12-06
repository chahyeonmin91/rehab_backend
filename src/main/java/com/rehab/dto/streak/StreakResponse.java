package com.rehab.dto.streak;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.rehab.domain.entity.UserStreak;
import com.rehab.dto.plan.ActivityHistoryDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Streak 상세 정보 응답")
public class StreakResponse {

	@Schema(description = "현재 연속 달성 일수", example = "7")
	private Integer currentStreak;

	@Schema(description = "최대 연속 달성 일수 (역대 최고)", example = "15")
	private Integer maxStreak;

	@Schema(description = "마지막 활동 날짜", example = "2025-12-01")
	private LocalDate lastActiveDate;

	@Schema(description = "활동 이력 (최근 N일)")
	private List<ActivityHistoryDto> activityHistory;

	/**
	 * Entity → DTO 변환 (활동 이력 제외)
	 */
	public static StreakResponse from(UserStreak streak) {
		return StreakResponse.builder()
			.currentStreak(streak.getCurrentStreak())
			.maxStreak(streak.getMaxStreak())
			.lastActiveDate(streak.getLastActiveDate())
			.build();
	}

	/**
	 * Entity + 활동 이력 → DTO 변환
	 */
	public static StreakResponse of(UserStreak streak, List<ActivityHistoryDto> activityHistory) {
		return StreakResponse.builder()
			.currentStreak(streak.getCurrentStreak())
			.maxStreak(streak.getMaxStreak())
			.lastActiveDate(streak.getLastActiveDate())
			.activityHistory(activityHistory)
			.build();
	}
}







