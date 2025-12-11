package com.rehab.service.medicationService;

import com.rehab.domain.entity.User;
import com.rehab.dto.medication.MedicationDto;

import java.time.LocalDate;
import java.util.List;

public interface MedicationService {

	MedicationDto.Response createMedication(User user, MedicationDto.CreateRequest request);

	List<MedicationDto.Response> getMyMedications(User user);

	MedicationDto.ScheduleResponse addSchedule(Long medicationId, MedicationDto.ScheduleRequest request);

	MedicationDto.MedicationLogResponse recordLog(Long medicationId, MedicationDto.LogRequest request);

	MedicationDto.Response updateMedication(User user, Long medicationId, MedicationDto.UpdateRequest request);

	MedicationDto.DailyScheduleResponse getSchedulesForDate(User user, LocalDate date);

}
