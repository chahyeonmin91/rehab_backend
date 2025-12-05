package com.rehab.service.authService;

import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.handler.UserHandler;
import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.LoginType;
import com.rehab.domain.repository.UserRepository;
import com.rehab.dto.auth.AuthRequest;
import com.rehab.dto.auth.AuthResponse;
import com.rehab.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	// TODO: refreshToken Redis 저장용 서비스/유틸 추가 예정

	@Override
	public AuthResponse.SignupResponse signup(AuthRequest.SignupRequest request) {

		// 1) 이메일 중복 체크
		userRepository.findByEmail(request.getEmail())
			.ifPresent(u -> { throw new UserHandler(ErrorStatus.DUPLICATE_USER_EMAIL); });

		// 2) 비밀번호 일치 검증
		if (!request.getPassword().equals(request.getPasswordCheck())) {
			// 적절한 에러코드 새로 추가해도 됨
			throw new UserHandler(ErrorStatus._BAD_REQUEST);
		}

		// 3) 이메일 인증 여부 체크 (추후 Redis 연동 가능)
		if (Boolean.FALSE.equals(request.getEmailVerified())) {
			throw new UserHandler(ErrorStatus._BAD_REQUEST); // "이메일 인증이 필요합니다."용 코드 추가해도 됨
		}

		// 4) 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 5) User 생성
		User user = User.createEmailUser(request.getEmail(), encodedPassword);
		user = userRepository.save(user);

		// 6) JWT 발급 (role 필드는 User 안에 이미 있을 거라고 가정: UserRole.USER)
		String role = user.getRole() != null ? user.getRole().name() : "USER";
		String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), role, null);
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), role);

		// TODO: refreshToken Redis 저장 로직 추가

		return AuthResponse.SignupResponse.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.loginType(LoginType.EMAIL)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
			.build();
	}

	@Override
	public AuthResponse.LoginResponse login(AuthRequest.LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new UserHandler(ErrorStatus.MEMBER_NOT_FOUND));

		// EMAIL 로그인 타입인지 확인 (카카오 계정은 여기 들어오면 안 되게)
		if (user.getLoginType() != LoginType.EMAIL) {
			throw new UserHandler(ErrorStatus._BAD_REQUEST);
		}

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UserHandler(ErrorStatus._BAD_REQUEST); // "비밀번호 불일치"용 코드 추가해도 좋음
		}

		String role = user.getRole() != null ? user.getRole().name() : "USER";
		String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), role, null);
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), role);

		// TODO: refreshToken Redis 저장

		return AuthResponse.LoginResponse.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.username(user.getUsername())
			.loginType(user.getLoginType())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}

