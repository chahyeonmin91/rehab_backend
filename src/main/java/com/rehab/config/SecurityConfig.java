package com.rehab.config;

import com.rehab.domain.repository.UserRepository;
import com.rehab.security.jwt.JwtAuthenticationFilter;
import com.rehab.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/health",
					"/auth/**",
					"/oauth2/**",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/oauth2/authorization/google")
				.defaultSuccessUrl("/auth/login-success", true)
				.failureUrl("/auth/login-failure")
				.userInfoEndpoint(userInfo -> {
					// 이후에 CustomOAuth2UserService 넣을 자리
					// userInfo.userService(customOAuth2UserService);
				})
			)
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(form -> form.disable())
			.logout(logout -> logout.disable());

		http.addFilterBefore(
			new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
			UsernamePasswordAuthenticationFilter.class
		);

		return http.build();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

