package com.rehab.service.tempService;

import org.springframework.stereotype.Service;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.TempHandler;

@Service
public class TempQueryServiceImpl implements TempQueryService {

	@Override
	public void CheckFlag(Integer flag) {
		if (flag == 1) {
			throw new TempHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}
