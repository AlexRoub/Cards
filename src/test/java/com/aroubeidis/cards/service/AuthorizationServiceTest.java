package com.aroubeidis.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import com.aroubeidis.cards.configuration.jwt.JwtService;
import com.aroubeidis.cards.entities.CardEntity;
import com.aroubeidis.cards.entities.UserEntity;
import com.aroubeidis.cards.exceptions.ForbiddenException;
import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.repository.CardRepository;
import com.aroubeidis.cards.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private CardRepository cardRepository;
	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthorizationService authorizationService;

	@Test
	void getCardAfterAuthorization_noToken_throwsForbiddenException() {

		final var headers = new HttpHeaders();

		when(jwtService.extractUsername(null)).thenReturn(null);
		when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

		assertThrows(ForbiddenException.class, () -> authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getCardAfterAuthorization_ADMIN() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
		when(cardRepository.findById(1L)).thenReturn(Optional.of(CardEntity.builder()
				.build()));
		assertNotNull(authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getCardAfterAuthorization_MEMBER_cardId_exists_and_user_matches_success() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.MEMBER)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		final var cardEntity = CardEntity.builder()
				.name("name")
				.description("description")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.user(user)
				.build();
		when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

		assertNotNull(authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getCardAfterAuthorization_MEMBER_cardId_exists_and_user_not_matches_throwsForbiddenException() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.MEMBER)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		final var otherUser = UserEntity.builder()
				.id(2L)
				.email("b@c.com")
				.role(Role.MEMBER)
				.build();
		final var cardEntity = CardEntity.builder()
				.name("name")
				.description("description")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.user(otherUser)
				.build();
		when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

		assertThrows(ForbiddenException.class, () -> authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getCardAfterAuthorization_MEMBER_cardId_not_exists_returnNull() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.MEMBER)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
		when(cardRepository.findById(1L)).thenReturn(Optional.empty());

		assertNull(authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getUser_success() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");

		final var user = UserEntity.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.MEMBER)
				.build();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		assertEquals(user, authorizationService.getUser(headers));
	}

	@Test
	void getUser_userNotFound_throwsForbiddenException() {

		final var headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 12345");

		when(jwtService.extractUsername("12345")).thenReturn("a@b.com");
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

		assertThrows(ForbiddenException.class, () -> authorizationService.getCardAfterAuthorization(headers, 1L));
	}

	@Test
	void getUser_noToken_throwsForbiddenException() {

		final var headers = new HttpHeaders();

		when(jwtService.extractUsername(null)).thenReturn(null);
		when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

		assertThrows(ForbiddenException.class, () -> authorizationService.getCardAfterAuthorization(headers, 1L));
	}
}