package com.rehab.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		// 기본 정보 로드
		OAuth2User oAuth2User = super.loadUser(userRequest);

		// provider 구분
		String provider = userRequest.getClientRegistration().getRegistrationId();  // kakao

		// 카카오만 처리
		if (provider.equals("kakao")) {
			return new CustomOAuth2User(oAuth2User.getAttributes());
		}

		return oAuth2User;
	}
}
