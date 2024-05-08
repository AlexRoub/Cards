package com.aroubeidis.cards.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.service.CardService;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

	@Mock
	private CardService cardService;

	@InjectMocks
	private CardController cardController;

	@Test
	void getCards_success() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
			.build();
		final var sort = new String[] { "creationDate", "desc" };

		final var getCardsVO = GetCardsVO.builder()
			.headers(headers)
			.page(0)
			.size(10)
			.sort(sort)
			.filters(filters)
			.build();

		final Page<CardResponse> responsePage = new PageImpl<>(List.of(CardResponse.builder()
			.build()));

		when(cardService.getAllCards(getCardsVO)).thenReturn(responsePage);

		final var response = cardController.getCards(headers, 0, 10, sort, filters);

		assertEquals(responsePage, response);
	}

	@Test
	void getCardById_success() {

		final var headers = new HttpHeaders();

		when(cardService.getCardById(headers, 1L)).thenReturn(CardResponse.builder()
			.build());

		final var expectedResponse = CardResponse.builder()
			.build();

		final var response = cardController.getCardById(headers, 1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	void getCardById_notFound() {

		final var headers = new HttpHeaders();

		when(cardService.getCardById(headers, 1L)).thenReturn(null);

		final var response = cardController.getCardById(headers, 1L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void createCard() {

		final var headers = new HttpHeaders();
		final var request = CreateCardRequest.builder()
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.build();

		final var cardResponse = CardResponse.builder()
			.id(1L)
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.status("To Do")
			.creationDate(LocalDate.now())
			.build();

		when(cardService.createCard(headers, request)).thenReturn(cardResponse);

		final var response = cardController.createCard(headers, request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(cardResponse, response.getBody());
	}

	@Test
	void updateCard_success()
			throws Exception {

		final var headers = new HttpHeaders();
		final var request = UpdateCardRequest.builder()
			.name("updated name")
			.build();

		final var cardResponse = CardResponse.builder()
			.id(1L)
			.name("updated name")
			.description("this is a task")
			.color("#ff0000")
			.status("To Do")
			.creationDate(LocalDate.now())
			.build();

		when(cardService.updateCard(headers, 1L, request)).thenReturn(cardResponse);

		final var response = cardController.updateCard(headers, 1L, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(cardResponse, response.getBody());
	}

	@Test
	void updateCard_notFound()
			throws Exception {

		final var headers = new HttpHeaders();
		final var request = UpdateCardRequest.builder()
			.name("updated name")
			.build();

		when(cardService.updateCard(headers, 1L, request)).thenReturn(null);

		final var response = cardController.updateCard(headers, 1L, request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void deleteCard_noContent() {

		final var headers = new HttpHeaders();

		when(cardService.deleteCard(headers, 1L)).thenReturn(true);

		final var response = cardController.deleteCard(headers, 1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void deleteCard_notFound() {

		final var headers = new HttpHeaders();

		when(cardService.deleteCard(headers, 1L)).thenReturn(false);

		final var response = cardController.deleteCard(headers, 1L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}