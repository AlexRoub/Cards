package com.aroubeidis.cards.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aroubeidis.cards.configuration.JwtService;
import com.aroubeidis.cards.entities.TokenDto;
import com.aroubeidis.cards.entities.UserDto;
import com.aroubeidis.cards.exceptions.ForbiddenException;
import com.aroubeidis.cards.model.TokenType;
import com.aroubeidis.cards.model.request.AuthenticationRequest;
import com.aroubeidis.cards.model.request.RegisterRequest;
import com.aroubeidis.cards.model.response.AuthenticationResponse;
import com.aroubeidis.cards.repository.TokenRepository;
import com.aroubeidis.cards.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(final RegisterRequest request) {

		final var user = UserDto.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.role(request.getRole())
			.build();

		final var savedUser = userRepository.save(user);

		final var jwtToken = jwtService.generateToken(user);
		final var refreshToken = jwtService.generateRefreshToken(user);

		saveUserToken(savedUser, jwtToken);

		return AuthenticationResponse.builder()
			.accessToken(jwtToken)
			.refreshToken(refreshToken)
			.build();
	}

	public AuthenticationResponse authenticate(final AuthenticationRequest request) {

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		final var user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> ForbiddenException.builder()
				.message("User doesn't exist.")
				.build());

		final var jwtToken = jwtService.generateToken(user);
		final var refreshToken = jwtService.generateRefreshToken(user);

		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);

		return AuthenticationResponse.builder()
			.accessToken(jwtToken)
			.refreshToken(refreshToken)
			.build();
	}

	private void saveUserToken(final UserDto user, final String jwtToken) {

		final var token = TokenDto.builder()
			.user(user)
			.token(jwtToken)
			.tokenType(TokenType.BEARER)
			.expired(false)
			.revoked(false)
			.build();

		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(final UserDto user) {

		final var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

		if (validUserTokens.isEmpty()) {
			return;
		}

		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});

		tokenRepository.saveAll(validUserTokens);
	}

	public void refreshToken(final HttpServletRequest request, final HttpServletResponse response)
		throws IOException {

		final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}

		final var refreshToken = authHeader.substring(7);
		final var userEmail = jwtService.extractUsername(refreshToken);

		if (userEmail != null) {
			final var user = userRepository.findByEmail(userEmail)
				.orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				final var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				final var authResponse = AuthenticationResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}
}
