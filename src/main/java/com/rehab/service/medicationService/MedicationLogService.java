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
import com.rehab.dto.medication.MedicationLogResponse;  // ← 이 import
import com.rehab.service.dailySummary.DailySummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

	@Transactional
	public MedicationLogResponse createMedicationLog(Long userId, CreateMedicationLogRequest request) {
		log.info("복약 로그 생성 - userId: {}, medicationId: {}", userId, request.getMedicationId());

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.USER_NOT_FOUND));

		Medication medication = medicationRepository.findById(request.getMedicationId())
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.MEDICATION_NOT_FOUND));

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

		try {
			dailySummaryService.updateDailySummary(userId, request.getTakenAt());
			log.info("일일 요약 업데이트 완료 - userId: {}, date: {}",
				userId, request.getTakenAt().toLocalDate());
		} catch (Exception e) {
			log.error("일일 요약 업데이트 실패 - userId: {}, date: {}, error: {}",
				userId, request.getTakenAt().toLocalDate(), e.getMessage(), e);
		}

		return convertToMedicationLogResponse(savedLog);
	}

	public List<MedicationLogResponse> getMedicationLogsByDateRange(
		Long userId, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("복약 로그 조회 - userId: {}, start: {}, end: {}", userId, startDate, endDate);

		List<MedicationLog> logs = medicationLogRepository
			.findByUser_UserIdAndTakenAtBetween(userId, startDate, endDate);

		return logs.stream()
			.map(this::convertToMedicationLogResponse)
			.collect(Collectors.toList());
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
