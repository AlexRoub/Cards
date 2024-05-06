package com.aroubeidis.cards.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.request.AuthenticationRequest;
import com.aroubeidis.cards.model.request.RegisterRequest;
import com.aroubeidis.cards.model.response.AuthenticationResponse;
import com.aroubeidis.cards.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

	@Mock
	private AuthenticationService authenticationService;

	@InjectMocks
	private AuthenticationController authenticationController;

	@Test
	void register() {

		final var request = RegisterRequest.builder()
				.email("a@b.comm")
				.password("1234")
				.role(Role.ADMIN)
				.build();

		final var expectedResponse = AuthenticationResponse.builder()
				.accessToken("1234")
				.refreshToken("4321")
				.build();
		when(authenticationService.register(request)).thenReturn(expectedResponse);

		final var response = authenticationController.register(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	void authenticate() {

		final var request = AuthenticationRequest.builder()
				.build();

		final var expectedResponse = AuthenticationResponse.builder()
				.accessToken("1234")
				.refreshToken("4321")
				.build();
		when(authenticationService.authenticate(request)).thenReturn(expectedResponse);

		final var response = authenticationController.authenticate(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	void refreshToken()
			throws IOException {

		final var request = mock(HttpServletRequest.class);
		final var response = mock(HttpServletResponse.class);

		authenticationController.refreshToken(request, response);

		verify(authenticationService, times(1)).refreshToken(request, response);
	}
}