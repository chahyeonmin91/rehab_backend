package com.rehab.service.medicationService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.Medication;
import com.rehab.domain.entity.MedicationLog;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.medication.MedicationLogRepository;
import com.rehab.domain.repository.medication.MedicationRepository;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.medication.CreateMedicationLogRequest;
import com.rehab.dto.medication.MedicationLogListResponse;
import com.rehab.dto.medication.MedicationLogResponse;  // ← 이 import
import com.rehab.service.dailySummary.DailySummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationLogService {

	private final MedicationLogRepository medicationLogRepository;
	private final MedicationRepository medicationRepository;
	private final UserRepository userRepository;
	private final DailySummaryService dailySummaryService;

	/**
	 * 복약 로그 생성
	 */
	@Transactional
	public MedicationLogResponse createMedicationLog(Long userId, CreateMedicationLogRequest request) {
		log.info("복약 로그 생성 - userId: {}, medicationId: {}", userId, request.getMedicationId());

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				log.error("사용자를 찾을 수 없음 - userId: {}", userId);
				return new RehabPlanException(ErrorStatus.USER_NOT_FOUND);
			});

		log.info("사용자 조회 성공 - userId: {}", user.getUserId());

		// 약물 조회
		Medication medication = medicationRepository.findById(request.getMedicationId())
			.orElseThrow(() -> {
				log.error("약물을 찾을 수 없음 - medicationId: {}", request.getMedicationId());
				return new RehabPlanException(ErrorStatus.MEDICATION_NOT_FOUND);
			});

		log.info("약물 조회 성공 - medicationId: {}", medication.getMedicationId());

		// 복약 로그 생성
		MedicationLog medicationLog = MedicationLog.builder()
			.user(user)
			.medication(medication)
			.takenAt(request.getTakenAt())
			.timeOfDay(request.getTimeOfDay())
			.taken(request.getTaken())
			.notes(request.getNotes())
			.build();

		MedicationLog savedLog = medicationLogRepository.save(medicationLog);

		log.info("복약 로그 생성 완료 - medicationLogId: {}", savedLog.getMedicationLogId());

		// 일일 요약 업데이트 (비동기적으로 처리, 실패해도 로그 생성은 성공)
		try {
			dailySummaryService.updateDailySummary(userId, request.getTakenAt());
			log.info("일일 요약 업데이트 완료 - userId: {}, date: {}",
				userId, request.getTakenAt().toLocalDate());
		} catch (Exception e) {
			log.error("일일 요약 업데이트 실패 - userId: {}, date: {}, error: {}",
				userId, request.getTakenAt().toLocalDate(), e.getMessage(), e);
			// DailySummary 업데이트 실패해도 복약 로그 생성은 성공으로 처리
		}

		return convertToMedicationLogResponse(savedLog);
	}

	/**
	 * 특정 날짜 복약 로그 조회
	 */
	public MedicationLogListResponse getMedicationLogsByDate(Long userId, LocalDate date) {
		log.info("복약 로그 조회 - userId: {}, date: {}", userId, date);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		List<MedicationLog> logs = medicationLogRepository
			.findByUser_UserIdAndTakenAtBetween(userId, startOfDay, endOfDay);

		List<MedicationLogResponse> logResponses = logs.stream()
			.map(this::convertToMedicationLogResponse)
			.collect(Collectors.toList());

		return MedicationLogListResponse.builder()
			.date(date.atStartOfDay())
			.logs(logResponses)
			.build();
	}

	/**
	 * MedicationLog -> MedicationLogResponse 변환
	 */
	private MedicationLogResponse convertToMedicationLogResponse(MedicationLog log) {
		return MedicationLogResponse.builder()
			.medicationLogId(log.getMedicationLogId())
			.userId(log.getUser().getUserId())
			.medicationId(log.getMedication().getMedicationId())
			.medicationName(log.getMedication().getName())
			.takenAt(log.getTakenAt())
			.timeOfDay(log.getTimeOfDay())
			.taken(log.getTaken())
			.notes(log.getNotes())
			.createdAt(log.getCreatedAt())
			.updatedAt(log.getUpdatedAt())
			.build();
	}
}
