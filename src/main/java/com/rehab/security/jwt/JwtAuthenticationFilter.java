package com.rehab.security.jwt;

import com.rehab.domain.entity.User;
import com.rehab.domain.repository.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null) {
			try {
				Long userId = jwtTokenProvider.getUserId(token);
				String role = jwtTokenProvider.getRole(token);

				User user = userRepository.findById(userId)
					.orElse(null);

				if (user != null) {
					var auth = new UsernamePasswordAuthenticationToken(
						user,
						null,
						List.of(new SimpleGrantedAuthority("ROLE_" + role))
					);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception e) {
				// 토큰 에러 시 그냥 통과시키고, 컨트롤러에서 @PreAuthorize 등으로 막히게
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}
}

