package com.rehab.service.emailService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

	private final StringRedisTemplate redisTemplate;
	private static final long EXPIRE_MINUTES = 5;

	public void saveVerificationCode(String email, String code) {
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		ops.set("email:verify:" + email, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
	}

	public String getVerificationCode(String email) {
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		return ops.get("email:verify:" + email);
	}

	public void deleteCode(String email) {
		redisTemplate.delete("email:verify:" + email);
	}

	public boolean isVerified(String email) {
		String val = getVerificationCode(email);
		return "VERIFIED".equals(val);
	}

	public void markVerified(String email) {
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		ops.set("email:verify:" + email, "VERIFIED", EXPIRE_MINUTES, TimeUnit.MINUTES);
	}
}
