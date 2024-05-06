package com.aroubeidis.cards.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.aroubeidis.cards.entities.TokenDto;
import com.aroubeidis.cards.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

	@Captor
	private ArgumentCaptor<TokenDto> tokenCaptor;

	@Mock
	private TokenRepository tokenRepository;

	@InjectMocks
	private LogoutService logoutService;

	@Test
	void logout_withExistingToken() {

		try (final var mockedStatic = mockStatic(SecurityContextHolder.class)) {
			final var request = mock(HttpServletRequest.class);
			final var response = mock(HttpServletResponse.class);

			when(request.getHeader("Authorization")).thenReturn("Bearer 12345");
			final var tokenDto = TokenDto.builder()
					.build();
			when(tokenRepository.findByToken("12345")).thenReturn(Optional.of(tokenDto));
			when(tokenRepository.save(tokenCaptor.capture())).thenReturn(tokenDto);

			logoutService.logout(request, response, null);

			final var captorValue = tokenCaptor.getValue();
			assertTrue(captorValue.isExpired());
			assertTrue(captorValue.isRevoked());

			mockedStatic.verify(SecurityContextHolder::clearContext, times(1));
		}
	}

	@Test
	void logout_withNonExistingToken() {

		try (final var mockedStatic = mockStatic(SecurityContextHolder.class)) {

			final var request = mock(HttpServletRequest.class);
			final var response = mock(HttpServletResponse.class);

			when(request.getHeader("Authorization")).thenReturn("Bearer 12345");
			when(tokenRepository.findByToken("12345")).thenReturn(Optional.empty());

			logoutService.logout(request, response, null);

			verify(tokenRepository, times(0)).save(any());
			mockedStatic.verify(SecurityContextHolder::clearContext, times(0));
		}
	}

	@Test
	void logout_noTokenInHeaders() {

		try (final var mockedStatic = mockStatic(SecurityContextHolder.class)) {

			final var request = mock(HttpServletRequest.class);
			final var response = mock(HttpServletResponse.class);

			when(request.getHeader("Authorization")).thenReturn(null);

			logoutService.logout(request, response, null);
			verifyNoInteractions(tokenRepository);
			mockedStatic.verify(SecurityContextHolder::clearContext, times(0));
		}
	}
}