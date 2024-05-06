package com.aroubeidis.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aroubeidis.cards.entities.UserDto;
import com.aroubeidis.cards.model.Role;

import io.jsonwebtoken.ExpiredJwtException;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	@BeforeEach
	public void setUp() {

		ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
		ReflectionTestUtils.setField(jwtService, "jwtExpiration", 604800000);
		ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000);
	}

	@Test
	void extractUsername() {

		assertEquals("ab@gmail.com",
				jwtService.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYkBnbWFpbC5jb20iLCJpYXQiOjE3MTUwMDM5NzIsImV4cCI6MTcxNTA5MDM3Mn0"
						+ ".-Pp2nyHEOO9xBglbmMKpLsE_kRJ2E3Fc2JRSJdARTlA"));
	}

	@Test
	void generateToken() {

		final var user = UserDto.builder()
				.id(1L)
				.email("ab@gmail.com")
				.password("12345")
				.role(Role.MEMBER)
				.build();

		final var response = jwtService.generateToken(user);
		assertNotNull(response);
		assertTrue(response.contains("eyJhbGciOiJIUzI1NiJ9"));
	}

	@Test
	void generateRefreshToken() {

		final var user = UserDto.builder()
				.id(1L)
				.email("ab@gmail.com")
				.password("12345")
				.role(Role.MEMBER)
				.build();

		final var response = jwtService.generateRefreshToken(user);
		assertNotNull(response);
		assertTrue(response.contains("eyJhbGciOiJIUzI1NiJ9"));
	}

	@Test
	void isTokenValid_returnTrue() {

		final var token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYkBnbWFpbC5jb20iLCJpYXQiOjE3MTUwMDM5NzIsImV4cCI6MTcxNTA5MDM3Mn0"
				+ ".-Pp2nyHEOO9xBglbmMKpLsE_kRJ2E3Fc2JRSJdARTlA";
		final var user = UserDto.builder()
				.id(1L)
				.email("ab@gmail.com")
				.role(Role.MEMBER)
				.build();

		assertTrue(jwtService.isTokenValid(token, user));
	}

	@Test
	void isTokenValid_username_differs_returnFalse() {

		final var token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYkBnbWFpbC5jb20iLCJpYXQiOjE3MTUwMDM5NzIsImV4cCI6MTcxNTA5MDM3Mn0"
				+ ".-Pp2nyHEOO9xBglbmMKpLsE_kRJ2E3Fc2JRSJdARTlA";
		final var user = UserDto.builder()
				.id(1L)
				.email("a@gmail.com")
				.role(Role.MEMBER)
				.build();

		assertFalse(jwtService.isTokenValid(token, user));
	}

	@Test
	void isTokenValid_token_isExpired_returnFalse() {

		final var token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb3ViQGdtYWlsLmNvbSIsImlhdCI6MTcxNDc0MDU5NiwiZXhwIjoxNzE0ODI2OTk2fQ"
				+ ".5zs5FXKlVDq_zG8wSop6bip_XFpooOm45blBXzXOR-A";
		final var user = UserDto.builder()
				.id(1L)
				.email("ab@gmail.com")
				.role(Role.MEMBER)
				.build();

		assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
	}
}