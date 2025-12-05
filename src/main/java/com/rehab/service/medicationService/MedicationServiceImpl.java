package com.rehab.service.medicationService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.MediSchedule;
import com.rehab.domain.entity.Medication;
import com.rehab.domain.entity.MedicationLog;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.MedicationStatus;
import com.rehab.domain.repository.medication.MediScheduleRepository;
import com.rehab.domain.repository.medication.MedicationLogRepository;
import com.rehab.domain.repository.medication.MedicationRepository;
import com.rehab.dto.medication.MedicationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationServiceImpl implements MedicationService {

	private final MedicationRepository medicationRepository;
	private final MediScheduleRepository scheduleRepository;
	private final MedicationLogRepository logRepository;

	@Override
	public MedicationDto.Response createMedication(User user, MedicationDto.CreateRequest request) {

		Medication medication = Medication.builder()
			.user(user)
			.name(request.getName())
			.dose(request.getDose())
			.route(request.getRoute())
			.instructions(request.getInstructions())
			.description(request.getDescription())
			.status(MedicationStatus.ACTIVE)
			.build();

		medicationRepository.save(medication);

		return toResponse(medication);
	}

	@Override
	public List<MedicationDto.Response> getMyMedications(User user) {
		return medicationRepository.findByUser(user)
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Override
	public MedicationDto.ScheduleResponse addSchedule(Long medicationId, MedicationDto.ScheduleRequest request) {

		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new UserHandler(ErrorStatus._BAD_REQUEST));

		MediSchedule schedule = MediSchedule.builder()
			.medication(medication)
			.timeOfDay(request.getTimeOfDay())
			.notify(request.getNotify())
			.rrule(request.getRrule())
			.build();

		scheduleRepository.save(schedule);

		return MedicationDto.ScheduleResponse.builder()
			.scheduleId(schedule.getMediScheduleId())
			.timeOfDay(schedule.getTimeOfDay())
			.notify(schedule.getNotify())
			.rrule(schedule.getRrule())
			.build();
	}

	@Override
	public MedicationDto.MedicationLogResponse recordLog(Long medicationId, MedicationDto.LogRequest request) {

		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new UserHandler(ErrorStatus._BAD_REQUEST));

		MedicationLog log = MedicationLog.builder()
			.user(medication.getUser())
			.medication(medication)
			.timeOfDay(request.getTimeOfDay())
			.taken(request.getTaken())
			.notes(request.getNotes())
			.takenAt(LocalDateTime.now())
			.build();

		logRepository.save(log);

		return MedicationDto.MedicationLogResponse.builder()
			.logId(log.getMedicationLogId())
			.timeOfDay(log.getTimeOfDay())
			.taken(log.getTaken())
			.notes(log.getNotes())
			.takenAt(log.getTakenAt())
			.build();
	}

	private MedicationDto.Response toResponse(Medication med) {
		return MedicationDto.Response.builder()
			.medicationId(med.getMedicationId())
			.name(med.getName())
			.dose(med.getDose())
			.route(med.getRoute())
			.instructions(med.getInstructions())
			.description(med.getDescription())
			.status(med.getStatus())
			.schedules(
				med.getMediSchedules().stream()
					.map(s -> MedicationDto.ScheduleResponse.builder()
						.scheduleId(s.getMediScheduleId())
						.timeOfDay(s.getTimeOfDay())
						.notify(s.getNotify())
						.rrule(s.getRrule())
						.build())
					.toList()
			)
			.logs(
				med.getMedicationLogs().stream()
					.map(l -> MedicationDto.MedicationLogResponse.builder()
						.logId(l.getMedicationLogId())
						.timeOfDay(l.getTimeOfDay())
						.taken(l.getTaken())
						.notes(l.getNotes())
						.takenAt(l.getTakenAt())
						.build())
					.toList()
			)
			.build();
	}
}

