package com.rehab.domain.repository.report;

import com.rehab.domain.entity.ReportSnapshot;
import com.rehab.domain.entity.enums.ReportPeriod;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {

	/**
	 * 특정 사용자의 특정 기간 타입의 리포트 목록 조회 (최신순)
	 */
	List<ReportSnapshot> findByUser_UserIdAndPeriodOrderByGeneratedAtDesc(
		Long userId,
		ReportPeriod period,
		Pageable pageable
	);

	/**
	 * 특정 사용자의 모든 리포트 목록 조회 (최신순, 페이징)
	 */
	List<ReportSnapshot> findByUser_UserIdOrderByGeneratedAtDesc(
		Long userId,
		Pageable pageable
	);

	/**
	 * 특정 사용자의 특정 날짜 범위를 커버하는 주간 리포트 조회
	 */
	@Query(value = "SELECT * FROM report_snapshot rs " +
		"WHERE rs.user_id = :userId " +
		"AND rs.period = 'WEEKLY' " +
		"AND JSON_UNQUOTE(JSON_EXTRACT(rs.covered_range, '$.start')) = :rangeStart " +
		"AND JSON_UNQUOTE(JSON_EXTRACT(rs.covered_range, '$.end')) = :rangeEnd " +
		"LIMIT 1",
		nativeQuery = true)
	Optional<ReportSnapshot> findWeeklyReportByRange(
		@Param("userId") Long userId,
		@Param("rangeStart") String rangeStart,
		@Param("rangeEnd") String rangeEnd
	);
}
