package com.rehab.common.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class CodeGenerator {
	public String generate6DigitCode() {
		Random random = new Random();
		return String.format("%06d", random.nextInt(1000000));
	}
}
