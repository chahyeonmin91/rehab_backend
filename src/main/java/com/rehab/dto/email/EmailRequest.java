package com.rehab.dto.email;

import lombok.Getter;
import lombok.Setter;

public class EmailRequest {

	@Getter
	@Setter
	public static class Send {
		private String email;
	}

	@Getter
	@Setter
	public static class Verify {
		private String email;
		private String authCode;
	}
}

