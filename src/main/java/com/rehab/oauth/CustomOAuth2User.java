package com.rehab.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

	private final Map<String, Object> attributes;

	public CustomOAuth2User(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return attributes.get("id").toString(); // providerId
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public String getProviderId() {
		return attributes.get("id").toString();
	}

	public String getEmail() {
		try {
			return ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getNickname() {
		try {
			Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
			return profile.get("nickname").toString();
		} catch (Exception e) {
			return "사용자";
		}
	}

	public String getProfileImageUrl() {
		try {
			Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
			return profile.get("profile_image_url").toString();
		} catch (Exception e) {
			return null;
		}
	}
}
