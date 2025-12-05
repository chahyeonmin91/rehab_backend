package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.User;
import com.rehab.dto.reminder.ReminderDto;
import com.rehab.service.reminderService.ReminderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reminders")
@Tag(name = "Reminder", description = "알림 관리 API")
public class ReminderController {

	private final ReminderService reminderService;

	@PostMapping
	public ApiResponse<ReminderDto.Response> create(
		@AuthenticationPrincipal User user,
		@RequestBody ReminderDto.CreateRequest request
	) {
		return ApiResponse.onSuccess(reminderService.createReminder(user, request));
	}

	@GetMapping
	public ApiResponse<List<ReminderDto.Response>> getMyReminders(
		@AuthenticationPrincipal User user
	) {
		return ApiResponse.onSuccess(reminderService.getMyReminders(user));
	}

	@PatchMapping("/{id}")
	public ApiResponse<ReminderDto.Response> update(
		@PathVariable Long id,
		@RequestBody ReminderDto.UpdateRequest request
	) {
		return ApiResponse.onSuccess(reminderService.updateReminder(id, request));
	}
}
