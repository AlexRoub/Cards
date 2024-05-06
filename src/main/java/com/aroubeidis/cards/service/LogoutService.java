package com.aroubeidis.cards.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.aroubeidis.cards.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

	private final TokenRepository tokenRepository;

	@Override
	public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {

		final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}

		final var jwt = authHeader.substring(7);

		final var storedToken = tokenRepository.findByToken(jwt)
			.orElse(null);

		if (storedToken != null) {
			storedToken.setExpired(true);
			storedToken.setRevoked(true);

			tokenRepository.save(storedToken);

			SecurityContextHolder.clearContext();
		}
	}
}
