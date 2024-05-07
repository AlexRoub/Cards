package com.aroubeidis.cards.configuration.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class JwtService {

	@Value("${spring.security.jwt.secret}")
	private String secretKey;
	@Value("${spring.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${spring.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	public String extractUsername(final String token) {

		return extractClaim(token, Claims::getSubject);
	}

	public String generateToken(final UserDetails userDetails) {

		return buildToken(Maps.newHashMap(), userDetails, jwtExpiration);
	}

	public String generateRefreshToken(final UserDetails userDetails) {

		return buildToken(Maps.newHashMap(), userDetails, refreshExpiration);
	}

	public boolean isTokenValid(final String token, final UserDetails userDetails) {

		final var username = extractUsername(token);
		return username.equals(userDetails.getUsername());
	}

	private <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {

		final var claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private String buildToken(final Map<String, Object> extraClaims, final UserDetails userDetails, final long expiration) {

		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	private Claims extractAllClaims(final String token) {

		return Jwts.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key getSignInKey() {

		final var keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}