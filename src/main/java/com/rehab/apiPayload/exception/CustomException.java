package com.rehab.apiPayload.exception;

import com.rehab.apiPayload.code.status.ErrorStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorStatus errorStatus;

	public CustomException(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
		this.errorStatus = errorStatus;
	}
}

