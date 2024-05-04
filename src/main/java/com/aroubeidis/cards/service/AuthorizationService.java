package com.aroubeidis.cards.service;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.aroubeidis.cards.configuration.JwtService;
import com.aroubeidis.cards.entities.CardDto;
import com.aroubeidis.cards.entities.UserDto;
import com.aroubeidis.cards.exceptions.ForbiddenException;
import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.repository.CardRepository;
import com.aroubeidis.cards.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final JwtService jwtService;

	public void checkAuthorization(final HttpHeaders headers, final Long cardId) {

		final var user = getUser(headers);
		final var userId = user.getId();
		final var role = user.getRole();

		//ADMIN is considered to have access in all cards
		if (role == Role.MEMBER) {
			final var card = cardRepository.findById(cardId);

			card.map(CardDto::getUser)
				.map(UserDto::getId)
				.filter(userId::equals)
				.orElseThrow(() -> ForbiddenException.builder()
					.message("User is not authorized for this action.")
					.build());
		}
	}

	public UserDto getUser(final HttpHeaders headers) {

		final var token = getToken(headers);
		final var email = jwtService.extractUsername(token);

		return userRepository.findByEmail(email)
			.orElseThrow(() -> ForbiddenException.builder()
				.message("User doesn't exist.")
				.build());
	}

	private String getToken(final HttpHeaders headers) {

		return Optional.ofNullable(headers.getFirst(HttpHeaders.AUTHORIZATION))
			.map(auth -> auth.substring(7))
			.orElse(null);
	}
}
