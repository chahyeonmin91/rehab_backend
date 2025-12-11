package com.rehab.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DietCategory {
	BREAKFAST("아침식사"),
	LUNCH("점심식사"),
	DINNER("저녁식사"),
	SNACK("간식"),
	SUPPLEMENT("보충제");

	private final String description;
}
