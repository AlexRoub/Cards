package com.aroubeidis.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aroubeidis.cards.configuration.jwt.JwtService;
import com.aroubeidis.cards.entities.TokenEntity;
import com.aroubeidis.cards.entities.UserEntity;
import com.aroubeidis.cards.exceptions.ForbiddenException;
import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.TokenType;
import com.aroubeidis.cards.model.request.AuthenticationRequest;
import com.aroubeidis.cards.model.request.RegisterRequest;
import com.aroubeidis.cards.repository.TokenRepository;
import com.aroubeidis.cards.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Captor
	private ArgumentCaptor<Iterable<TokenEntity>> iterableTokenCaptor;

	@Mock
	private UserRepository userRepository;
	@Mock
	private TokenRepository tokenRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtService jwtService;
	@Mock
	private ServletOutputStream outputStream;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private AuthenticationService authenticationService;

	@Test
	void register() {

		final var request = RegisterRequest.builder()
				.email("a@b.com")
				.password("1234")
				.role(Role.ADMIN)
				.build();

		when(passwordEncoder.encode(request.getPassword())).thenReturn("fa54dags");

		final var user = UserEntity.builder()
				.email("a@b.com")
				.password("fa54dags")
				.role(Role.ADMIN)
				.build();
		when(userRepository.save(any())).thenReturn(user);

		final var accessToken = "eyJhbGciOiJIUzI1NiJ9";
		when(jwtService.generateToken(user)).thenReturn(accessToken);
		final var refreshToken = "eyJhbGciOiJIgGte45U";
		when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

		final var tokenEntity = TokenEntity.builder()
				.user(user)
				.token(accessToken)
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		when(tokenRepository.save(tokenEntity)).thenReturn(tokenEntity);

		final var response = authenticationService.register(request);

		assertEquals(accessToken, response.getAccessToken());
		assertEquals(refreshToken, response.getRefreshToken());
	}

	@Test
	void authenticate_userExists_tokenFound_success() {

		final var request = AuthenticationRequest.builder()
				.email("a@b.com")
				.password("1234")
				.build();

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.password("fa54dags")
				.role(Role.ADMIN)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		final var accessToken = "eyJhbGciOiJIUzI1NiJ9";
		when(jwtService.generateToken(user)).thenReturn(accessToken);
		final var refreshToken = "eyJhbGciOiJIgGte45U";
		when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

		final var tokenEntity = TokenEntity.builder()
				.user(user)
				.token(accessToken)
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(List.of(tokenEntity));
		when(tokenRepository.saveAll(iterableTokenCaptor.capture())).thenReturn(List.of(tokenEntity));

		final var response = authenticationService.authenticate(request);
		assertEquals(accessToken, response.getAccessToken());
		assertEquals(refreshToken, response.getRefreshToken());

		iterableTokenCaptor.getValue()
				.forEach(entity -> assertTrue(entity.isExpired()));
		iterableTokenCaptor.getValue()
				.forEach(entity -> assertTrue(entity.isRevoked()));

		verify(tokenRepository).save(any());
	}

	@Test
	void authenticate_userExists_tokenNotFound_success() {

		final var request = AuthenticationRequest.builder()
				.email("a@b.com")
				.password("1234")
				.build();

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.password("fa54dags")
				.role(Role.ADMIN)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		final var accessToken = "eyJhbGciOiJIUzI1NiJ9";
		when(jwtService.generateToken(user)).thenReturn(accessToken);
		final var refreshToken = "eyJhbGciOiJIgGte45U";
		when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(Collections.emptyList());

		final var response = authenticationService.authenticate(request);
		assertEquals(accessToken, response.getAccessToken());
		assertEquals(refreshToken, response.getRefreshToken());

		verify(tokenRepository, times(0)).saveAll(any());
		verify(tokenRepository).save(any());
	}

	@Test
	void authenticate_userNotExists_throwsForbiddenException() {

		final var request = AuthenticationRequest.builder()
				.email("a@b.com")
				.password("1234")
				.build();

		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

		assertThrows(ForbiddenException.class, () -> authenticationService.authenticate(request));
	}

	@Test
	void refreshToken_withExistingUserAndToken()
			throws IOException {

		final var request = mock(HttpServletRequest.class);
		final var response = mock(HttpServletResponse.class);

		when(request.getHeader("Authorization")).thenReturn("Bearer 12345");

		final var email = "a@b.com";
		when(jwtService.extractUsername("12345")).thenReturn(email);

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(jwtService.isTokenValid("12345", user)).thenReturn(true);
		when(jwtService.generateToken(user)).thenReturn("54321");
		when(response.getOutputStream()).thenReturn(outputStream);

		final var tokenEntity = TokenEntity.builder()
				.user(user)
				.token("54321")
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(List.of(tokenEntity));
		when(tokenRepository.saveAll(iterableTokenCaptor.capture())).thenReturn(List.of(tokenEntity));

		authenticationService.refreshToken(request, response);

		iterableTokenCaptor.getValue()
				.forEach(entity -> assertTrue(entity.isExpired()));
		iterableTokenCaptor.getValue()
				.forEach(entity -> assertTrue(entity.isRevoked()));

		verify(tokenRepository).save(any());
		verify(objectMapper).writeValue((OutputStream) any(), any());
	}

	@Test
	void refreshToken_withNonExistingUser()
			throws IOException {

		final var request = mock(HttpServletRequest.class);
		final var response = mock(HttpServletResponse.class);

		when(request.getHeader("Authorization")).thenReturn("Bearer 12345");
		when(jwtService.extractUsername("12345")).thenReturn(null);

		authenticationService.refreshToken(request, response);

		verifyNoInteractions(userRepository);
		verify(jwtService, times(0)).isTokenValid(any(), any());
		verify(jwtService, times(0)).generateToken(any());
	}

	@Test
	void refreshToken_withInvalidToken()
			throws IOException {

		final var request = mock(HttpServletRequest.class);
		final var response = mock(HttpServletResponse.class);

		when(request.getHeader("Authorization")).thenReturn("Bearer 12345");

		final var email = "a@b.com";
		when(jwtService.extractUsername("12345")).thenReturn(email);

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(jwtService.isTokenValid(any(), any())).thenReturn(false);

		authenticationService.refreshToken(request, response);

		verify(jwtService, times(0)).generateToken(any());
		verify(tokenRepository, times(0)).findAllValidTokenByUser(anyLong());
		verify(tokenRepository, times(0)).saveAll(any());
		verify(tokenRepository, times(0)).save(any());
	}

	@Test
	void refreshToken_withNoToken()
			throws IOException {

		final var request = mock(HttpServletRequest.class);
		final var response = mock(HttpServletResponse.class);

		when(request.getHeader("Authorization")).thenReturn(null);

		authenticationService.refreshToken(request, response);
		verifyNoInteractions(jwtService);
		verifyNoInteractions(tokenRepository);
	}
}