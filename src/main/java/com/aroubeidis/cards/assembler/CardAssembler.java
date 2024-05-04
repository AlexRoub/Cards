package com.aroubeidis.cards.assembler;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.aroubeidis.cards.entities.CardDto;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.response.CardResponse;

@Component
public class CardAssembler {

	public CardResponse toModel(final CardDto dto) {

		return Optional.ofNullable(dto)
			.map(this::buildCardResponse)
			.orElse(null);
	}

	public Page<CardResponse> toModelPage(final Page<CardDto> cardPage) {

		final var cards = cardPage.getContent()
			.stream()
			.filter(Objects::nonNull)
			.map(this::buildCardResponse)
			.toList();

		return new PageImpl<CardResponse>(cards,
			PageRequest.of(cardPage.getNumber(), cardPage.getSize(), cardPage.getSort()),
			cardPage.getTotalElements());
	}

	private CardResponse buildCardResponse(@NonNull final CardDto dto) {

		final var status = Optional.ofNullable(dto.getStatus())
			.map(Status::getValue)
			.orElse(null);

		return CardResponse.builder()
			.id(dto.getId())
			.name(dto.getName())
			.description(dto.getDescription())
			.color(dto.getColor())
			.status(status)
			.creationDate(dto.getCreationDate())
			.build();
	}
}
