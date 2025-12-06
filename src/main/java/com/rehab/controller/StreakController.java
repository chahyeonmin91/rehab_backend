package com.rehab.controller;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.dto.streak.StreakResponse;
import com.rehab.service.streak.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/streak")
@RequiredArgsConstructor
@Tag(name = "7. Streak & RecoveryScore", description = "Streak 및 회복 점수 관리 API")
public class StreakController {

	private final StreakService streakService;

	@GetMapping
	@Operation(
		summary = "Streak 상세 조회",
		description = """
            사용자의 연속 달성(Streak) 정보를 조회합니다.

            **활동 기준:**
            - 운동 완료율 ≥ 60% OR 복약 완료율 ≥ 70%

            **응답 포함 정보:**
            - 현재 연속 달성 일수 (currentStreak)
            - 최대 연속 달성 일수 (maxStreak)
            - 마지막 활동 날짜 (lastActiveDate)
            - 최근 N일간 활동 이력 (activityHistory)
            """
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = StreakResponseWrapper.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "사용자를 찾을 수 없음"
		)
	})
	public ApiResponse<StreakResponse> getStreak(
		@Parameter(
			description = "사용자 ID",
			required = true,
			example = "1"
		)
		@RequestParam Long userId,

		@Parameter(
			description = "조회할 최근 일수 (기본값: 30일, 최대: 90일)",
			example = "30"
		)
		@RequestParam(required = false, defaultValue = "30") Integer range
	) {
		// range 제한 (최대 90일)
		int rangeDays = Math.min(range, 90);

		StreakResponse response = streakService.getStreak(userId, rangeDays);
		return ApiResponse.onSuccess(response);
	}

	// Swagger 문서화용 래퍼 클래스
	@Schema(description = "Streak 조회 응답")
	private static class StreakResponseWrapper {
		@Schema(description = "성공 여부", example = "true")
		public Boolean isSuccess;

		@Schema(description = "응답 코드", example = "200")
		public String code;

		@Schema(description = "응답 메시지", example = "요청에 성공하였습니다.")
		public String message;

		@Schema(description = "응답 데이터")
		public StreakResponse result;
	}
}
