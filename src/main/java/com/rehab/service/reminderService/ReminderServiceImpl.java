package com.rehab.service.reminderService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.Reminder;
import com.rehab.domain.entity.User;
import com.rehab.domain.repository.ReminderRepository;
import com.rehab.dto.reminder.ReminderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {

	private final ReminderRepository reminderRepository;

	@Override
	public ReminderDto.Response createReminder(User user, ReminderDto.CreateRequest request) {

		Reminder reminder = Reminder.builder()
			.user(user)
			.type(request.getType())
			.channel(request.getChannel())
			.rule(request.getRule())
			.enabled(true)
			.nextFireAt(null) // 나중에 스케줄러 계산 가능
			.build();

		reminderRepository.save(reminder);

		return toResponse(reminder);
	}

	@Override
	public List<ReminderDto.Response> getMyReminders(User user) {
		return reminderRepository.findByUser(user)
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Override
	public ReminderDto.Response updateReminder(Long reminderId, ReminderDto.UpdateRequest request) {

		Reminder reminder = reminderRepository.findById(reminderId)
			.orElseThrow(() -> new UserHandler(ErrorStatus.ALARM_NOT_FOUND));

		if (request.getChannel() != null) reminder.setChannel(request.getChannel());
		if (request.getRule() != null) reminder.setRule(request.getRule());
		if (request.getEnabled() != null) reminder.setEnabled(request.getEnabled());

		return toResponse(reminder);
	}


	private ReminderDto.Response toResponse(Reminder reminder) {
		return ReminderDto.Response.builder()
			.reminderId(reminder.getReminderId())
			.type(reminder.getType())
			.channel(reminder.getChannel())
			.rule(reminder.getRule())
			.enabled(reminder.getEnabled())
			.nextFireAt(reminder.getNextFireAt())
			.build();
	}
}
