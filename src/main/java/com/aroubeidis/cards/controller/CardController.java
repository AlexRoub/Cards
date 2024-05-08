package com.aroubeidis.cards.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {

	private final CardService cardService;

	@PostMapping
	public Page<CardResponse> getCards(@RequestHeader final HttpHeaders headers,
			@RequestParam(defaultValue = "${constants.defaultPage:0}") final int page,
			@RequestParam(defaultValue = "${constants.defaultSize:10}") final int size,
			@RequestParam(required = false, defaultValue = "creationDate, desc") final String[] sort,
			@RequestBody final RequestFilters filters) {

		final var getCardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(page)
				.size(size)
				.sort(sort)
				.filters(filters)
				.build();

		return cardService.getAllCards(getCardsVO);
	}

	@GetMapping("/{cardId}")
	public ResponseEntity<CardResponse> getCardById(@RequestHeader final HttpHeaders headers, @PathVariable final Long cardId) {

		final var card = cardService.getCardById(headers, cardId);

		return Optional.ofNullable(card)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound()
						.build());
	}

	@PostMapping("/create")
	public ResponseEntity<CardResponse> createCard(@RequestHeader final HttpHeaders headers,
			@RequestBody @Valid final CreateCardRequest createCardRequest) {

		final var card = cardService.createCard(headers, createCardRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(card);
	}

	@PatchMapping("/{cardId}")
	public ResponseEntity<CardResponse> updateCard(@RequestHeader final HttpHeaders headers,
			@PathVariable final Long cardId,
			@RequestBody @Valid final UpdateCardRequest request)
			throws Exception {

		final var updatedCard = cardService.updateCard(headers, cardId, request);

		return Optional.ofNullable(updatedCard)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound()
						.build());
	}

	@DeleteMapping("/{cardId}")
	public ResponseEntity<Void> deleteCard(@RequestHeader final HttpHeaders headers, @PathVariable final Long cardId) {

		final var deleted = cardService.deleteCard(headers, cardId);

		return deleted
				? ResponseEntity.noContent()
				.build()
				: ResponseEntity.notFound()
						.build();
	}
}
