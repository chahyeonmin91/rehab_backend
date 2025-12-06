package com.rehab.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "진행률 리포트 응답")
public class ProgressReportResponse {

	@Schema(description = "조회 기간", example = "7d")
	private String range;

	@Schema(description = "시작 날짜", example = "2025-11-25T00:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate;

	@Schema(description = "종료 날짜", example = "2025-12-01T23:59:59")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate;

	@Schema(description = "운동 통계")
	private ExerciseStats exerciseStats;

	@Schema(description = "복약 통계")
	private MedicationStats medicationStats;

	@Schema(description = "통증 통계")
	private PainStats painStats;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "운동 통계")
	public static class ExerciseStats {

		@Schema(description = "평균 완료율(%)", example = "82")
		private Integer avgCompletionRate;

		@Schema(description = "총 운동 시간(초)", example = "8400")
		private Long totalDurationSec;

		@Schema(description = "일별 운동 데이터")
		private List<DailyExerciseData> dailyData;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "일별 운동 데이터")
	public static class DailyExerciseData {

		@Schema(description = "날짜", example = "2025-11-25T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime date;

		@Schema(description = "완료율(%)", example = "75")
		private Integer completionRate;

		@Schema(description = "운동 시간(초)", example = "1200")
		private Integer durationSec;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "복약 통계")
	public static class MedicationStats {

		@Schema(description = "평균 완료율(%)", example = "92")
		private Integer avgCompletionRate;

		@Schema(description = "일별 복약 데이터")
		private List<DailyMedicationData> dailyData;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "일별 복약 데이터")
	public static class DailyMedicationData {

		@Schema(description = "날짜", example = "2025-11-25T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime date;

		@Schema(description = "완료율(%)", example = "100")
		private Integer completionRate;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "통증 통계")
	public static class PainStats {

		@Schema(description = "평균 통증 점수 (1-10)", example = "5")
		private Integer avgPainScore;

		@Schema(description = "일별 통증 데이터")
		private List<DailyPainData> dailyData;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "일별 통증 데이터")
	public static class DailyPainData {

		@Schema(description = "날짜", example = "2025-11-25T00:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime date;

		@Schema(description = "평균 통증 점수", example = "6")
		private Integer avgPain;
	}
}
