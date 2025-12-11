package com.rehab.apiPayload.exception.handler;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.exception.GeneralException;

public class ExerciseHandler extends GeneralException {
	public ExerciseHandler(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
