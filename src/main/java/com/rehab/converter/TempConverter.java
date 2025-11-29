package com.rehab.converter;

import com.rehab.dto.temp.TempResponse;

public class TempConverter {

	public static TempResponse.TempTestDto toTempTestDto() {
		return TempResponse.TempTestDto.builder()
			.testString("This is Test")
			.build();
	}

	public static TempResponse.TempExceptionDTO toTempExceptionDto(Integer flag) {
		return TempResponse.TempExceptionDTO.builder()
			.flag(flag)
			.build();
	}
}
