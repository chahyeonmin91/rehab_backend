package com.rehab.service.reminderService;

import com.rehab.domain.entity.User;
import com.rehab.dto.reminder.ReminderDto;

import java.util.List;

public interface ReminderService {

	ReminderDto.Response createReminder(User user, ReminderDto.CreateRequest request);

	ReminderDto.Response updateReminder(User user, Long id, ReminderDto.UpdateRequest request);

	List<ReminderDto.Response> getMyReminders(User user);
}

