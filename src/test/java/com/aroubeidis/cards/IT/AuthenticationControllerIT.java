package com.aroubeidis.cards.IT;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.request.AuthenticationRequest;
import com.aroubeidis.cards.model.request.RegisterRequest;
import com.github.tomakehurst.wiremock.common.Json;

public class AuthenticationControllerIT extends AbstractIT {

	@Test
	public void register()
			throws Exception {

		final var headers = new HttpHeaders();
		final var registerRequest = RegisterRequest.builder()
				.email("abc@gmail.com")
				.password("12345")
				.role(Role.ADMIN)
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(registerRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty());
	}

	@Test
	public void authenticate()
			throws Exception {

		final var headers = new HttpHeaders();
		final var authenticationRequest = AuthenticationRequest.builder()
				.email("abcd@gmail.com")
				.password("password15")
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(authenticationRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty());
	}

	@Test
	public void refreshToken()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
						.headers(headers))
				.andExpect(status().isOk());
	}
}
