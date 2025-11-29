package com.rehab.apiPayload.exception.handler;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
