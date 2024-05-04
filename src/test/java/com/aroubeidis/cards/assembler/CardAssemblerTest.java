package com.aroubeidis.cards.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.aroubeidis.cards.entities.CardDto;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.response.CardResponse;

@ExtendWith(MockitoExtension.class)
class CardAssemblerTest {

	@InjectMocks
	private CardAssembler cardAssembler;

	@Test
	void toModel_entity_exists_success() {

		final var cardDto = CardDto.builder()
			.id(1L)
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.status(Status.TO_DO)
			.creationDate(LocalDate.now())
			.build();

		final var response = cardAssembler.toModel(cardDto);

		final var expectedResponse = CardResponse.builder()
			.id(1L)
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.status("To Do")
			.creationDate(LocalDate.now())
			.build();

		assertEquals(expectedResponse, response);
	}

	@Test
	void toModel_entity_null_expectNull() {

		assertNull(cardAssembler.toModel(null));
	}

	@Test
	void toModelPage_entity_exists_success() {

		final var cardDto = CardDto.builder()
			.id(1L)
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.status(Status.TO_DO)
			.creationDate(LocalDate.now())
			.build();

		final var response = cardAssembler.toModelPage(new PageImpl<CardDto>(List.of(cardDto)));

		final var expectedResponse = List.of(CardResponse.builder()
			.id(1L)
			.name("name")
			.description("this is a task")
			.color("#ff0000")
			.status("To Do")
			.creationDate(LocalDate.now())
			.build());

		assertEquals(expectedResponse, response.getContent());
		assertEquals(0, response.getNumber());
		assertEquals(1, response.getSize());
		assertEquals(1, response.getTotalElements());
		assertEquals(1, response.getTotalPages());
	}

	@Test
	void toModelPage_entity_null_success() {

		final var response = cardAssembler.toModelPage(new PageImpl<CardDto>(Collections.emptyList(), PageRequest.of(0, 10), 0));

		assertEquals(Collections.emptyList(), response.getContent());
		assertEquals(0, response.getNumber());
		assertEquals(10, response.getSize());
		assertEquals(0, response.getTotalElements());
		assertEquals(0, response.getTotalPages());
	}
}