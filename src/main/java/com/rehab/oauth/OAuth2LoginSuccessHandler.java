package com.rehab.oauth;

import com.rehab.domain.entity.User;
import com.rehab.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		User user = (User) authentication.getPrincipal();

		String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole().name(), null);
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getRole().name());

		String redirectUrl =
			"http://localhost:3000/auth?accessToken=" + accessToken +
				"&refreshToken=" + refreshToken;

		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}

