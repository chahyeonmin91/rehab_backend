package com.rehab.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리포트 스냅샷 목록 응답")
public class ReportSnapshotListResponse {

	@Schema(description = "리포트 스냅샷 목록")
	private List<ReportSnapshotItem> snapshots;

	@Schema(description = "총 개수", example = "5")
	private Integer totalCount;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Schema(description = "리포트 스냅샷 항목")
	public static class ReportSnapshotItem {

		@Schema(description = "리포트 스냅샷 ID", example = "4001")
		private Long reportSnapshotId;

		@Schema(description = "기간 타입", example = "WEEKLY")
		private String period;

		@Schema(description = "커버하는 날짜 범위")
		private DateRangeDto coveredRange;

		@Schema(description = "주간 하이라이트 메시지",
			example = "7일 연속 운동 달성!")
		private String weeklyHighlight;

		@Schema(description = "집계된 메트릭 (JSON)",
			example = "{\"totalExercises\":28}")
		private String metrics;

		@Schema(description = "회복 예측 점수", example = "78.50")
		private BigDecimal recoveryPrediction;

		@Schema(description = "리포트 생성 시각", example = "2025-12-01T23:00:00")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime generatedAt;
	}

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
