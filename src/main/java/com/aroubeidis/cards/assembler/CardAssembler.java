package com.aroubeidis.cards.assembler;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.aroubeidis.cards.dto.CardDto;
import com.aroubeidis.cards.model.response.CardResponse;

@Component
public class CardAssembler {

	public CardResponse toModel(final CardDto dto) {

		return Optional.ofNullable(dto)
			.map(this::buildCardResponse)
			.orElse(null);
	}

	public Page<CardResponse> toModel(final Page<CardDto> cardPage) {

		final var cards = cardPage.getContent()
			.stream()
			.filter(Objects::nonNull)
			.map(this::buildCardResponse)
			.toList();

		return new PageImpl<CardResponse>(cards,
			PageRequest.of(cardPage.getNumber(), cardPage.getSize(), cardPage.getSort()),
			cardPage.getTotalElements());
	}

	private CardResponse buildCardResponse(final CardDto dto) {

		return CardResponse.builder()
			.id(dto.getId())
			.name(dto.getName())
			.description(dto.getDescription())
			.color(dto.getColor())
			.status(dto.getStatus()
				.getValue())
			.creationDate(dto.getCreationDate())
			.build();
	}
}
