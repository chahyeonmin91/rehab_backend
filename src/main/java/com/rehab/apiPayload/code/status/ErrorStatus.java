package com.rehab.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	/// SMS 인증 관련 에러
	UNAUTHORIZED_SMS(HttpStatus.BAD_REQUEST, "SMS4001", "SMS 인증 실패, 인증 번호가 일치하지 않습니다."),

	/// 이미지 관련 오류
	NOT_IMAGE(HttpStatus.BAD_REQUEST, "IMG4001", "이미지가 없습니다."),

	/// 알람 관련 오류
	ALARM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ALA4001", "알림이 존재하지 않습니다."),

	/// 채팅 관련 에러
	CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT4001", "존재하지 않는 채팅입니다."),
	MESSAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT4002", "존재하지 않는 메시지 입니다."),

	/// 일반적인 에러
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	/// 멤버 관련 에러
	USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
	NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
	MEMBERS_ROLE_IS_USER(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자의 ROLE이 USER입니다."),
	MEMBERS_ROLE_IS_PRO(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자의 ROLE이 PRO입니다."),

	/// 인증 관련 오류
	INVALID_JWT_ISSUE(HttpStatus.BAD_REQUEST, "JWT4001", "유효한 JWT 토큰이 아닙니다."),
	INVALID_JWT_ISSUE_REFRESH(HttpStatus.BAD_REQUEST, "JWT4002", "유효한 Refresh 토큰이 아닙니다."),
	DUPLICATE_USER_EMAIL(HttpStatus.BAD_REQUEST, "AUTH4001", "이미 가입된 이메일입니다."),
	AUTH_MISSING_REFRESH_COOKIE(HttpStatus.UNAUTHORIZED, "A001", "refreshToken을 찾을 수 없습니다."),
	AUTH_INVALID_OR_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "refreshToken이 유효하지 않습니다."),
	AUTH_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "refreshToken이 만료되었습니다."),
	AUTH_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A004", "refreshToken에 해당하는 유저를 찾지 못했습니다."),
	AUTH_STORED_REFRESH_NULL(HttpStatus.UNAUTHORIZED, "A005", "저장된 refreshToken이 null값입니다."),
	AUTH_REFRESH_MISMATCH(HttpStatus.UNAUTHORIZED, "A006", "refreshToken이 mismatch입니다."),

	// 재활 플랜
	REHAB_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_001", "재활 플랜을 찾을 수 없습니다."),
	NO_ACTIVE_PLAN(HttpStatus.NOT_FOUND, "PLAN_002", "활성화된 재활 플랜이 없습니다."),

	// 플랜 항목
	PLAN_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_003", "플랜 항목을 찾을 수 없습니다."),

	// 운동
	EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE_001", "운동 정보를 찾을 수 없습니다."),

	// 운동 로그
	EXERCISE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "LOG_001", "운동 로그를 찾을 수 없습니다."),
	INVALID_PAIN_SCORE(HttpStatus.BAD_REQUEST, "LOG_002", "통증 점수는 1-10 사이여야 합니다."),
	INVALID_COMPLETION_RATE(HttpStatus.BAD_REQUEST, "LOG_003", "완료율은 0-100 사이여야 합니다."),

	// 일일 요약
	DAILY_SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "SUMMARY_001", "일일 요약을 찾을 수 없습니다."),

	AI_INFERENCE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_001", "AI 추론에 실패했습니다."),
	AI_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI_002", "AI 서버를 사용할 수 없습니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build()
			;
	}
}
