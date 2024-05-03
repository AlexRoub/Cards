package com.aroubeidis.cards.configuration;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aroubeidis.cards.repository.TokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final TokenRepository tokenRepository;

	@Override
	protected void doFilterInternal(@NonNull final HttpServletRequest request,
		@NonNull final HttpServletResponse response,
		@NonNull final FilterChain filterChain)
		throws ServletException, IOException {

		if (request.getServletPath()
			.contains("/api/v1/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		final var authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final var jwt = authHeader.substring(7);
		final var userEmail = jwtService.extractUsername(jwt);
		if (userEmail != null
			&& SecurityContextHolder.getContext()
			.getAuthentication() == null) {
			final var userDetails = userDetailsService.loadUserByUsername(userEmail);
			final var isTokenValid = tokenRepository.findByToken(jwt)
				.map(t -> !t.isExpired() && !t.isRevoked())
				.orElse(false);
			if (jwtService.isTokenValid(jwt, userDetails) && Boolean.TRUE.equals(isTokenValid)) {
				final var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext()
					.setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}