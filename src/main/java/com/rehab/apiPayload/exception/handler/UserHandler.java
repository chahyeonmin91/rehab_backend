package com.rehab.apiPayload.exception.handler;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {

	public UserHandler(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
