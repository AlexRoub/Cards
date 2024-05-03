package com.aroubeidis.cards.controller;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aroubeidis.cards.assembler.CardAssembler;
import com.aroubeidis.cards.dto.CardDto;
import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.service.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {

	private final CardService cardService;
	private final CardAssembler cardAssembler;

	@GetMapping
	public ResponseEntity<Page<CardDto>> getCards(@RequestParam(defaultValue = "${constants.defaultPage}") final int page,
		@RequestParam(defaultValue = "${constants.defaultSize}") final int size,
		@RequestParam(required = false, defaultValue = "creationDate, desc") final String[] sort,
		@RequestParam(required = false) final RequestFilters filters)
		throws BadRequestException {

		final var getCardsVO = GetCardsVO.builder()
			.page(page)
			.size(size)
			.sort(sort)
			.filters(filters)
			.build();

		final var cardPage = cardService.getAllCards(getCardsVO);

		return ResponseEntity.ok(cardPage);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CardResponse> getCardById(@PathVariable final Long id) {

		final var card = cardService.getCardById(id);

		return Optional.ofNullable(card)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound()
				.build());
	}

	@PostMapping
	public ResponseEntity<CardResponse> createCard(@RequestBody final CreateCardRequest createCardRequest)
		throws BadRequestException {

		final var card = cardService.createCard(createCardRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(card);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CardResponse> updateCard(@PathVariable final Long id, @RequestBody final UpdateCardRequest request) {

		final var updatedCard = cardService.updateCard(id, request);

		return Optional.ofNullable(updatedCard)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound()
				.build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCard(@PathVariable final Long id) {

		final var deleted = cardService.deleteCard(id);

		return deleted
			? ResponseEntity.noContent()
			.build()
			: ResponseEntity.notFound()
				.build();
	}
}
