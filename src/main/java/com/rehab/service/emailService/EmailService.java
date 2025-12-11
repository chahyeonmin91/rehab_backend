package com.rehab.service.emailService;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	public void sendVerificationCode(String email, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[RehabCoach] 이메일 인증코드");
		message.setText("인증코드: " + code + "\n5분 안에 입력해주세요.");

		mailSender.send(message);
	}
}
