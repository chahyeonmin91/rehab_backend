package com.rehab.dto.medication;

import com.rehab.domain.entity.enums.MedicationStatus;
import com.rehab.domain.entity.enums.TimeOfDay;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class MedicationDto {

	// 복약 등록 요청
	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class CreateRequest {
		private String name;
		private String dose;
		private String route;
		private String instructions;
		private String description;
	}

	// 복약 정보 응답
	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class Response {
		private Long medicationId;
		private String name;
		private String dose;
		private String route;
		private String instructions;
		private String description;
		private MedicationStatus status;
		private List<ScheduleResponse> schedules;
		private List<MedicationLogResponse> logs;
	}

	// 스케줄
	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class ScheduleRequest {
		private TimeOfDay timeOfDay;
		private Boolean notify;
		private String rrule;
	}

	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class ScheduleResponse {
		private Long scheduleId;
		private TimeOfDay timeOfDay;
		private Boolean notify;
		private String rrule;
	}

	// 복약 로그 기록 요청
	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class LogRequest {
		private TimeOfDay timeOfDay;
		private Boolean taken;
		private String notes;
	}

	// 복약 로그 응답
	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class MedicationLogResponse {
		private Long logId;
		private TimeOfDay timeOfDay;
		private Boolean taken;
		private String notes;
		private LocalDateTime takenAt;
	}
}

