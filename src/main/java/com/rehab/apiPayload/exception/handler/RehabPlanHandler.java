package com.rehab.apiPayload.exception.handler;

import com.rehab.apiPayload.code.BaseErrorCode;
import com.rehab.apiPayload.exception.GeneralException;

public class RehabPlanHandler extends GeneralException {
  public RehabPlanHandler(BaseErrorCode errorCode) {
	  super(errorCode);
  }
}
