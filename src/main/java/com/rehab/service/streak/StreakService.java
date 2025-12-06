package com.rehab.service.streak;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.DailySummary;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.UserStreak;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.plan.ActivityHistoryDto;
import com.rehab.dto.streak.StreakResponse;
import com.rehab.domain.repository.dailySummary.DailySummaryRepository;
import com.rehab.domain.repository.streak.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreakService {

	private final UserStreakRepository streakRepository;
	private final UserRepository userRepository;
	private final DailySummaryRepository dailySummaryRepository;

	// 활동 기준
	private static final int EXERCISE_THRESHOLD = 60;  // 운동 완료율 60% 이상
	private static final int MEDICATION_THRESHOLD = 70;  // 복약 완료율 70% 이상

	/**
	 * 사용자 Streak 조회 (활동 이력 포함)
	 * @param userId 사용자 ID
	 * @param rangeDays 조회할 최근 일수 (기본값: 30일)
	 * @return StreakResponse
	 */
	public StreakResponse getStreak(Long userId, Integer rangeDays) {
		UserStreak streak = streakRepository.findByUserId(userId)
			.orElseGet(() -> createInitialStreak(userId));

		// 활동 이력 조회
		int days = rangeDays != null ? rangeDays : 30;
		List<ActivityHistoryDto> activityHistory = getActivityHistory(userId, days);

		return StreakResponse.of(streak, activityHistory);
	}

	/**
	 * 사용자 Streak 조회 (간단 버전 - 홈 화면용)
	 */
	public StreakResponse getStreakSimple(Long userId) {
		UserStreak streak = streakRepository.findByUserId(userId)
			.orElseGet(() -> createInitialStreak(userId));

		return StreakResponse.from(streak);
	}

	/**
	 * DailySummary 확정 시 Streak 업데이트
	 *
	 * @param userId 사용자 ID
	 * @param date 날짜
	 * @param exerciseCompletionRate 운동 완료율
	 * @param medicationCompletionRate 복약 완료율
	 */
	@Transactional
	public void updateStreakFromDailySummary(
		Long userId,
		LocalDate date,
		Integer exerciseCompletionRate,
		Integer medicationCompletionRate
	) {
		UserStreak streak = streakRepository.findByUserId(userId)
			.orElseGet(() -> createAndSaveInitialStreak(userId));

		boolean isActive = isActivityCriteriaMet(exerciseCompletionRate, medicationCompletionRate);

		if (isActive) {
			int newStreak = streak.incrementStreak(date);
			log.info("Streak updated for user={}, date={}, newStreak={}", userId, date, newStreak);
		} else {
			streak.resetStreak(date);
			log.info("Streak reset for user={}, date={} (criteria not met)", userId, date);
		}

		streakRepository.save(streak);
	}

	/**
	 * 활동 기준 충족 여부 판단
	 * - 운동 완료율 ≥ 60% OR 복약 완료율 ≥ 70%
	 */
	private boolean isActivityCriteriaMet(Integer exerciseRate, Integer medicationRate) {
		boolean exerciseMet = exerciseRate != null && exerciseRate >= EXERCISE_THRESHOLD;
		boolean medicationMet = medicationRate != null && medicationRate >= MEDICATION_THRESHOLD;
		return exerciseMet || medicationMet;
	}

	/**
	 * 활동 이력 조회 (최근 N일)
	 */
	private List<ActivityHistoryDto> getActivityHistory(Long userId, int days) {
		// 오늘 00:00 기준으로 계산 (필요하면 now() 그대로 써도 됨)
		LocalDateTime endDateTime = LocalDate.now().atStartOfDay();
		LocalDateTime startDateTime = endDateTime.minusDays(days - 1);

		// DailySummary 조회 (LocalDateTime 범위로)
		List<DailySummary> summaries = dailySummaryRepository
			.findByUserIdAndDateBetween(userId, startDateTime, endDateTime.plusDays(1));
		// end 를 inclusive 로 쓰고 싶으면 +1일 해서 [start, end+1) 로 잡는 게 안전함

		// Map으로 변환 (LocalDateTime → Summary)
		Map<LocalDateTime, DailySummary> summaryMap = summaries.stream()
			.collect(Collectors.toMap(
				DailySummary::getDate,      // LocalDateTime
				Function.identity()
			));

		// 결과 리스트 생성 (하루 단위로 채우기)
		List<ActivityHistoryDto> result = new ArrayList<>();

		for (LocalDateTime cur = startDateTime;
			 !cur.isAfter(endDateTime);
			 cur = cur.plusDays(1)) {

			DailySummary summary = summaryMap.get(cur);

			ActivityHistoryDto dto = (summary != null)
				? ActivityHistoryDto.from(summary)
				: ActivityHistoryDto.createEmpty(cur);

			result.add(dto);
		}

		return result;
	}

	/**
	 * 초기 Streak 생성 (메모리에만)
	 */
	private UserStreak createInitialStreak(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

		return UserStreak.createInitial(user);
	}

	/**
	 * 초기 Streak 생성 및 저장
	 */
	@Transactional
	public UserStreak createAndSaveInitialStreak(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

		UserStreak streak = UserStreak.createInitial(user);
		return streakRepository.save(streak);
	}

	/**
	 * 배치: 오래된 streak 정리
	 * - 마지막 활동이 어제 이전인데 currentStreak > 0인 경우 리셋
	 */
	@Transactional
	public int cleanupStaleStreaks() {
		LocalDate today = LocalDate.now();
		List<UserStreak> staleStreaks = streakRepository.findStaleStreaks(today);

		int resetCount = 0;
		for (UserStreak streak : staleStreaks) {
			if (streak.getLastActiveDate().isBefore(today.minusDays(1))) {
				streak.resetStreak(today);
				resetCount++;
			}
		}

		log.info("Cleaned up {} stale streaks", resetCount);
		return resetCount;
	}

	/**
	 * 통계: 현재 활성 streak 사용자 수
	 */
	public long countActiveStreaks() {
		return streakRepository.countActiveStreaks();
	}
}







