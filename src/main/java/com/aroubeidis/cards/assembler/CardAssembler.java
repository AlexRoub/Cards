package com.aroubeidis.cards.assembler;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.aroubeidis.cards.dto.CardDto;
import com.aroubeidis.cards.model.response.CardResponse;

@Component
public class CardAssembler {

	public CardResponse toModel(@NonNull final CardDto dto) {

		return CardResponse.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.color(dto.getColor())
			.status(dto.getStatus())
			.creationDate(dto.getCreationDate())
			.build();
	}
}
