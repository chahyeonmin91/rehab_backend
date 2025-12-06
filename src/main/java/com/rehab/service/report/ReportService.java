package com.rehab.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.rehab.dto.report.ProgressReportResponse;
import com.rehab.dto.report.ReportSnapshotListResponse;
import com.rehab.dto.report.WeeklyReportResponse;

public interface ReportService {

	/**
	 * 진행률 리포트 조회
	 *
	 * @param userId  사용자 ID
	 * @param range   조회 범위 (예: "7d", "14d", "30d")
	 * @param endDate 범위의 끝 시간 (null이면 현재 날짜의 23:59:59 기준)
	 * @return 진행률 리포트 응답
	 */
	ProgressReportResponse getProgressReport(Long userId, String range, LocalDateTime endDate);

	/**
	 * 주간 하이라이트 리포트 조회
	 *
	 * @param userId   사용자 ID
	 * @param weekStart 주간 시작일 (null이면 이번 주 월요일 기준)
	 * @return 주간 리포트 응답
	 */
	WeeklyReportResponse getWeeklyReport(Long userId, LocalDate weekStart);

	/**
	 * 리포트 스냅샷 목록 조회
	 *
	 * @param userId 사용자 ID
	 * @param period 기간 (예: "WEEKLY" / null 이면 전체)
	 * @param limit  가져올 개수 (null 또는 0 이하면 기본 10개)
	 * @return 리포트 스냅샷 목록 응답
	 */
	ReportSnapshotListResponse getReportSnapshots(Long userId, String period, Integer limit);
}
