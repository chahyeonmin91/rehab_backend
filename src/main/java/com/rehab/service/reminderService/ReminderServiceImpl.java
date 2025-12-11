package com.rehab.service.reminderService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.Reminder;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.remind.ReminderRepository;
import com.rehab.dto.reminder.ReminderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {

	private final ReminderRepository reminderRepository;

	@Override
	public ReminderDto.Response createReminder(User user, ReminderDto.CreateRequest request) {

		LocalDateTime next = calculateNextFireAt(request.getRule());

		Reminder reminder = Reminder.builder()
			.user(user)
			.type(request.getType())
			.channel(request.getChannel())
			.rule(request.getRule())
			.enabled(request.getEnabled())
			.nextFireAt(next)
			.build();

		reminderRepository.save(reminder);

		return toResponse(reminder);
	}

	@Override
	public ReminderDto.Response updateReminder(User user, Long id, ReminderDto.UpdateRequest request) {

		Reminder reminder = reminderRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Reminder not found"));

		if (!reminder.getUser().getUserId().equals(user.getUserId())) {
			throw new RuntimeException("Unauthorized");
		}

		LocalDateTime next = calculateNextFireAt(request.getRule());

		if (request.getRule() != null)
			reminder.setRule(request.getRule());

		if (request.getEnabled() != null)
			reminder.setEnabled(request.getEnabled());

		reminder.setNextFireAt(next);

		reminderRepository.save(reminder);

		return toResponse(reminder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReminderDto.Response> getMyReminders(User user) {
		return reminderRepository.findByUser(user)
			.stream()
			.map(this::toResponse)
			.toList();
	}

	private LocalDateTime calculateNextFireAt(String rule) {
		try {
			int hour = Integer.parseInt(rule.replaceAll("[^0-9]", ""));
			return LocalDateTime.now().withHour(hour).withMinute(0).withSecond(0);
		} catch (Exception e) {
			return LocalDateTime.now();
		}
	}

	private ReminderDto.Response toResponse(Reminder reminder) {
		return ReminderDto.Response.builder()
			.reminderId(reminder.getReminderId())
			.type(reminder.getType())
			.channel(reminder.getChannel())
			.rule(reminder.getRule())
			.enabled(reminder.getEnabled())
			.nextFireAt(reminder.getNextFireAt())
			.createdAt(reminder.getCreatedAt())
			.updatedAt(reminder.getUpdatedAt())
			.build();
	}
}
