package com.rehab.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportPeriod {
	WEEKLY("주간", 7),
	MONTHLY("월간", 30);

	private final String description;
	private final int days;
}
