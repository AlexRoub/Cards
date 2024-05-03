package com.aroubeidis.cards.configuration;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
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

	public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {

		final var claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(final UserDetails userDetails) {

		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {

		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	public String generateRefreshToken(final UserDetails userDetails) {

		return buildToken(new HashMap<>(), userDetails, refreshExpiration);
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

	public boolean isTokenValid(final String token, final UserDetails userDetails) {

		final var username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(final String token) {

		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(final String token) {

		return extractClaim(token, Claims::getExpiration);
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