package com.rehab.service.streak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Streak 관리 스케줄러
 * - 매일 자정에 끊긴 streak 정리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreakScheduler {

	private final StreakService streakService;

	/**
	 * 매일 자정 1분에 오래된 streak 정리
	 * - 마지막 활동이 어제 이전인데 currentStreak > 0인 경우 리셋
	 *
	 * cron: 초 분 시 일 월 요일
	 * "0 1 0 * * *" = 매일 00:01:00
	 */
	@Scheduled(cron = "0 1 0 * * *")
	public void cleanupStaleStreaks() {
		log.info("Starting stale streak cleanup batch job");

		try {
			int resetCount = streakService.cleanupStaleStreaks();
			log.info("Stale streak cleanup completed. Reset count: {}", resetCount);
		} catch (Exception e) {
			log.error("Error during stale streak cleanup", e);
		}
	}

	/**
	 * 매시간 활성 streak 통계 로깅 (선택 사항)
	 * - 모니터링용
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void logStreakStatistics() {
		try {
			long activeCount = streakService.countActiveStreaks();
			log.info("Current active streaks: {}", activeCount);
		} catch (Exception e) {
			log.error("Error logging streak statistics", e);
		}
	}
}
