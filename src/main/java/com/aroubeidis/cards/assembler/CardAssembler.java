package com.aroubeidis.cards.assembler;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.aroubeidis.cards.entities.CardEntity;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.response.CardResponse;

@Component
public class CardAssembler {

	public CardResponse toModel(final CardEntity entity) {

		return Optional.ofNullable(entity)
				.map(this::buildCardResponse)
				.orElse(null);
	}

	public Page<CardResponse> toModelPage(final Page<CardEntity> cardPage) {

		final var cards = cardPage.getContent()
				.stream()
				.filter(Objects::nonNull)
				.map(this::buildCardResponse)
				.toList();

		return new PageImpl<CardResponse>(cards,
				PageRequest.of(cardPage.getNumber(), cardPage.getSize(), cardPage.getSort()),
				cardPage.getTotalElements());
	}

	private CardResponse buildCardResponse(@NonNull final CardEntity entity) {

		final var status = Optional.ofNullable(entity.getStatus())
				.map(Status::getValue)
				.orElse(null);

		return CardResponse.builder()
				.id(entity.getId())
				.name(entity.getName())
				.description(entity.getDescription())
				.color(entity.getColor())
				.status(status)
				.creationDate(entity.getCreationDate())
				.build();
	}
}
