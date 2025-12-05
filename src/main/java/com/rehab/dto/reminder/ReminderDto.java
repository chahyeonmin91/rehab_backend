package com.rehab.dto.reminder;

import com.rehab.domain.entity.enums.ReminderChannel;
import com.rehab.domain.entity.enums.ReminderType;
import lombok.*;

import java.time.LocalDateTime;

public class ReminderDto {

	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class CreateRequest {
		private ReminderType type;
		private ReminderChannel channel;
		private String rule; // JSON (RRULE)
	}

	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class UpdateRequest {
		private ReminderChannel channel;
		private String rule;
		private Boolean enabled;
	}

	@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
	public static class Response {
		private Long reminderId;
		private ReminderType type;
		private ReminderChannel channel;
		private String rule;
		private Boolean enabled;
		private LocalDateTime nextFireAt;
	}
}
