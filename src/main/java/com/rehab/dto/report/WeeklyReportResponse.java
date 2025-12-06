package com.rehab.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주간 하이라이트 응답")
public class WeeklyReportResponse {

	@Schema(description = "리포트 스냅샷 ID", example = "4001")
	private Long reportSnapshotId;

	@Schema(description = "사용자 ID", example = "123")
	private Long userId;

	@Schema(description = "기간 타입", example = "WEEKLY")
	private String period;

	@Schema(description = "커버하는 날짜 범위")
	private DateRangeDto coveredRange;

	@Schema(description = "주간 하이라이트 메시지",
		example = "7일 연속 운동 달성! 꾸준한 습관이 회복을 만듭니다.")
	private String weeklyHighlight;

	@Schema(description = "집계된 메트릭 (JSON)",
		example = "{\"totalExercises\":28,\"avgCompletionRate\":82}")
	private String metrics;

	@Schema(description = "회복 예측 점수", example = "78.50")
	private BigDecimal recoveryPrediction;

	@Schema(description = "리포트 생성 시각", example = "2025-12-01T23:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime generatedAt;

	@Schema(description = "생성 시각", example = "2025-12-01T23:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@Schema(description = "수정 시각", example = "2025-12-01T23:00:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "날짜 범위")
	public static class DateRangeDto {

		@Schema(description = "시작 날짜", example = "2025-11-25")
		private String start;

		@Schema(description = "종료 날짜", example = "2025-12-01")
		private String end;
	}
}







