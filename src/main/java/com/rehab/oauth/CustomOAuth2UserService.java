package com.rehab.oauth;

import com.rehab.domain.entity.User;
import com.rehab.domain.entity.enums.UserRole;
import com.rehab.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		String email = (String) kakaoAccount.get("email");
		String nickname = (String) profile.get("nickname");
		String profileImageUrl = (String) profile.get("profile_image_url");

		// DB 조회
		User user = userRepository.findByEmail(email)
			.orElseGet(() -> {
				// 없으면 생성
				return userRepository.save(User.builder()
					.email(email)
					.nickname(nickname)
					.profileImageUrl(profileImageUrl)
					.role(UserRole.USER)
					.build());
			});

		return new DefaultOAuth2User(
			List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
			attributes,
			"id"
		);
	}
}
