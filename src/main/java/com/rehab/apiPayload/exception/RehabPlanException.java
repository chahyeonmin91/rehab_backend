package com.rehab.apiPayload.exception;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 재활 플랜 관련 예외
 */
@Getter
@AllArgsConstructor
public class RehabPlanException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public ErrorReasonDTO getErrorReason() {
		return this.errorCode.getReason();
	}

	public ErrorReasonDTO getErrorReasonHttpStatus() {
		return this.errorCode.getReasonHttpStatus();
	}
}
