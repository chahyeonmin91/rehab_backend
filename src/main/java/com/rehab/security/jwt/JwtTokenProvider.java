package com.rehab.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

	private final Key key;

	@Getter
	private final long accessTokenValidityInSeconds;

	@Getter
	private final long refreshTokenValidityInSeconds;

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds,
		@Value("${jwt.refresh-token-validity-in-seconds:1209600}") long refreshTokenValidityInSeconds
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
		this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
	}

	public String createAccessToken(Long userId, String role, Map<String, Object> additionalClaims) {
		return createToken(userId, role, additionalClaims, accessTokenValidityInSeconds);
	}

	public String createRefreshToken(Long userId, String role) {
		return createToken(userId, role, null, refreshTokenValidityInSeconds);
	}

	private String createToken(Long userId, String role, Map<String, Object> additionalClaims, long validitySeconds) {
		Instant now = Instant.now();
		Instant expiry = now.plusSeconds(validitySeconds);

		JwtBuilder builder = Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("role", role)
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiry))
			.signWith(key, SignatureAlgorithm.HS256);

		if (additionalClaims != null) {
			additionalClaims.forEach(builder::claim);
		}

		return builder.compact();
	}

	public Jws<Claims> parseToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	public Long getUserId(String token) {
		return Long.valueOf(parseToken(token).getBody().getSubject());
	}

	public String getRole(String token) {
		return (String) parseToken(token).getBody().get("role");
	}
}
