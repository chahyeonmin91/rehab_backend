package com.rehab.domain.entity;

import com.rehab.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 일일 요약(Daily Summary) 엔티티
 * 하루 단위 운동/복약 완료율, 통증 점수 등을 집계
 */
@Entity
@Table(name = "daily_summary")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DailySummary extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "summary_id")
	private Long summaryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	/**
	 * 모든 운동 완료 여부
	 */
	@Column(name = "all_exercises_completed")
	private Boolean allExercisesCompleted;

	/**
	 * 운동 완료율 (0-100%)
	 */
	@Column(name = "exercise_completion_rate")
	private Integer exerciseCompletionRate;

	/**
	 * 모든 복약 완료 여부
	 */
	@Column(name = "all_medications_taken")
	private Boolean allMedicationsTaken;

	/**
	 * 복약 완료율 (0-100%)
	 */
	@Column(name = "medication_completion_rate")
	private Integer medicationCompletionRate;

	/**
	 * 평균 통증 점수 (1-10)
	 */
	@Column(name = "avg_pain_score")
	private Integer avgPainScore;

	/**
	 * 총 운동 시간 (초)
	 */
	@Column(name = "total_duration_sec")
	private Integer totalDurationSec;

	/**
	 * 일일 메트릭스 (JSON)
	 * 예: {"totalExercises": 3, "completedExercises": 3, "avgRpe": 5.0}
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "daily_metrics", columnDefinition = "json")
	private String  dailyMetrics;

	@Column(name = "all_diet_completed")
	private Boolean allDietCompleted;

	@Column(name = "diet_completion_rate")
	private Integer dietCompletionRate;

	// === 비즈니스 메서드 ===

	/**
	 * 운동 완료 여부 체크 (Streak 계산용)
	 */
	public boolean meetsExerciseCriteria() {
		return exerciseCompletionRate != null && exerciseCompletionRate >= 60;
	}

	/**
	 * 복약 완료 여부 체크 (Streak 계산용)
	 */
	public boolean meetsMedicationCriteria() {
		return medicationCompletionRate != null && medicationCompletionRate >= 70;
	}

	/**
	 * Streak 활동 기준 충족 여부
	 * (운동 60% 이상 OR 복약 70% 이상)
	 */
	public boolean meetsStreakCriteria() {
		return meetsExerciseCriteria() || meetsMedicationCriteria();
	}

	/**
	 * DailySummary 업데이트
	 */
	public void updateSummary(
		Boolean allExercisesCompleted,
		Integer exerciseCompletionRate,
		Boolean allMedicationsTaken,
		Integer medicationCompletionRate,
		Integer avgPainScore,
		Integer totalDurationSec,
		String dailyMetrics
	) {
		if (allExercisesCompleted != null) {
			this.allExercisesCompleted = allExercisesCompleted;
		}
		if (exerciseCompletionRate != null) {
			this.exerciseCompletionRate = exerciseCompletionRate;
		}
		if (allMedicationsTaken != null) {
			this.allMedicationsTaken = allMedicationsTaken;
		}
		if (medicationCompletionRate != null) {
			this.medicationCompletionRate = medicationCompletionRate;
		}
		if (avgPainScore != null) {
			this.avgPainScore = avgPainScore;
		}
		if (totalDurationSec != null) {
			this.totalDurationSec = totalDurationSec;
		}
		if (dailyMetrics != null) {
			this.dailyMetrics = dailyMetrics;
		}
	}
}
