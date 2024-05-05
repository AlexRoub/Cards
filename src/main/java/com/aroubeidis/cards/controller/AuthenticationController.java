package com.aroubeidis.cards.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aroubeidis.cards.model.request.AuthenticationRequest;
import com.aroubeidis.cards.model.request.RegisterRequest;
import com.aroubeidis.cards.model.response.AuthenticationResponse;
import com.aroubeidis.cards.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid final RegisterRequest request) {

		return ResponseEntity.ok(authenticationService.register(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid final AuthenticationRequest request) {

		return ResponseEntity.ok(authenticationService.authenticate(request));
	}

	@PostMapping("/refresh-token")
	public void refreshToken(final HttpServletRequest request, final HttpServletResponse response)
		throws IOException {

		authenticationService.refreshToken(request, response);
	}
}
