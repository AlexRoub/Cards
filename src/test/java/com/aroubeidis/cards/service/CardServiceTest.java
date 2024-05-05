package com.aroubeidis.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;

import com.aroubeidis.cards.assembler.CardAssembler;
import com.aroubeidis.cards.entities.CardDto;
import com.aroubeidis.cards.entities.UserDto;
import com.aroubeidis.cards.exceptions.BadRequestException;
import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.repository.CardRepository;
import com.aroubeidis.cards.utils.GeneralUtils;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

	@Mock
	private CardRepository cardRepository;
	@Mock
	private CardAssembler cardAssembler;
	@Mock
	private AuthorizationService authorizationService;

	@InjectMocks
	private CardService cardService;

	@Test
	void getAllCards_noFilters_userADMIN_returnPage() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
				.build();

		final var cardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(0)
				.size(10)
				.sort(new String[] { "name", "asc" })
				.filters(filters)
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		final var sortBy = GeneralUtils.getSort(cardsVO.getSort()[0], cardsVO.getSort()[1]);
		final var pageable = PageRequest.of(cardsVO.getPage(), cardsVO.getSize(), sortBy);
		final var cardDto1 = CardDto.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		final var cardDto2 = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS)
				.build();
		final var cardDtoPage = new PageImpl<CardDto>(List.of(cardDto2, cardDto1));
		when(cardRepository.findAll((Specification<CardDto>) any(), eq(pageable))).thenReturn(cardDtoPage);
		final var cardResponse1 = CardResponse.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS.getValue())
				.build();
		final var cardResponse2 = CardResponse.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		final var cardResponsePage = new PageImpl<CardResponse>(List.of(cardResponse1, cardResponse2));
		when(cardAssembler.toModelPage(cardDtoPage)).thenReturn(cardResponsePage);

		final var response = cardService.getAllCards(cardsVO);
		assertEquals(cardResponsePage, response);
	}

	@Test
	void getAllCards_withNameFilters_userADMIN_returnPage() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
				.name("name 1")
				.build();

		final var cardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(0)
				.size(Integer.MAX_VALUE)
				.sort(new String[] { "creationDate", "asc" })
				.filters(filters)
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		final var sortBy = GeneralUtils.getSort(cardsVO.getSort()[0], cardsVO.getSort()[1]);
		final var pageable = PageRequest.of(cardsVO.getPage(), Integer.MAX_VALUE, sortBy);
		final var cardDto = CardDto.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		final var cardDtoPage = new PageImpl<CardDto>(List.of(cardDto));
		when(cardRepository.findAll((Specification<CardDto>) any(), eq(pageable))).thenReturn(cardDtoPage);
		final var cardResponse = CardResponse.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		final var cardResponsePage = new PageImpl<CardResponse>(List.of(cardResponse));
		when(cardAssembler.toModelPage(cardDtoPage)).thenReturn(cardResponsePage);

		final var response = cardService.getAllCards(cardsVO);

		assertEquals(cardResponsePage, response);
	}

	@Test
	void getAllCards_withCreationDateAscSort_noFilters_userADMIN_returnPage() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
				.build();

		final var cardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(0)
				.size(10)
				.sort(new String[] { "creationDate", "asc" })
				.filters(filters)
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		final var sortBy = GeneralUtils.getSort(cardsVO.getSort()[0], cardsVO.getSort()[1]);
		final var pageable = PageRequest.of(cardsVO.getPage(), cardsVO.getSize(), sortBy);
		final var cardDto1 = CardDto.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		final var cardDto2 = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now()
						.minusDays(1))
				.status(Status.IN_PROGRESS)
				.build();
		final var cardDtoPage = new PageImpl<CardDto>(List.of(cardDto1, cardDto2));
		when(cardRepository.findAll((Specification<CardDto>) any(), eq(pageable))).thenReturn(cardDtoPage);
		final var cardResponse1 = CardResponse.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS.getValue())
				.build();
		final var cardResponse2 = CardResponse.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		final var cardResponsePage = new PageImpl<CardResponse>(List.of(cardResponse1, cardResponse2));
		when(cardAssembler.toModelPage(cardDtoPage)).thenReturn(cardResponsePage);

		final var response = cardService.getAllCards(cardsVO);

		assertEquals(cardResponsePage, response);
	}

	@Test
	void getAllCards_withCreationDateAscSort_noFilters_userMEMBER_returnPage() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
				.build();

		final var cardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(0)
				.size(10)
				.sort(new String[] { "creationDate", "asc" })
				.filters(filters)
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.MEMBER)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		final var sortBy = GeneralUtils.getSort(cardsVO.getSort()[0], cardsVO.getSort()[1]);
		final var pageable = PageRequest.of(cardsVO.getPage(), cardsVO.getSize(), sortBy);
		final var cardDto1 = CardDto.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		final var cardDto2 = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now()
						.minusDays(1))
				.status(Status.IN_PROGRESS)
				.build();
		final var cardDtoPage = new PageImpl<CardDto>(List.of(cardDto1, cardDto2));
		when(cardRepository.findAll((Specification<CardDto>) any(), eq(pageable))).thenReturn(cardDtoPage);
		final var cardResponse1 = CardResponse.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0011")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS.getValue())
				.build();
		final var cardResponse2 = CardResponse.builder()
				.name("name 2")
				.description("description 2")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		final var cardResponsePage = new PageImpl<CardResponse>(List.of(cardResponse1, cardResponse2));
		when(cardAssembler.toModelPage(cardDtoPage)).thenReturn(cardResponsePage);

		final var response = cardService.getAllCards(cardsVO);

		assertEquals(cardResponsePage, response);
	}

	@Test
	void getAllCards_withWrongSort_throwsBadRequest() {

		final var headers = new HttpHeaders();
		final var filters = RequestFilters.builder()
				.build();

		final var cardsVO = GetCardsVO.builder()
				.headers(headers)
				.page(0)
				.size(10)
				.sort(new String[] { "description", "asc" })
				.filters(filters)
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		assertThrows(BadRequestException.class, () -> cardService.getAllCards(cardsVO));

		verifyNoInteractions(cardRepository);
		verifyNoInteractions(cardAssembler);
	}

	@Test
	void getCardById_idExists_returnResponse() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;

		final var cardDto = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardDto));
		final var cardResponse = CardResponse.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		when(cardAssembler.toModel(cardDto)).thenReturn(cardResponse);

		final var response = cardService.getCardById(headers, cardId);
		assertEquals(cardResponse, response);

		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
	}

	@Test
	void getCardById_idDoesntExist_returnNull() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;

		when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

		assertNull(cardService.getCardById(headers, cardId));
		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
	}

	@Test
	void createCard_returnResponse() {

		final var headers = new HttpHeaders();
		final var request = CreateCardRequest.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.build();

		final var user = UserDto.builder()
				.id(1L)
				.email("a@b.com")
				.role(Role.ADMIN)
				.build();
		when(authorizationService.getUser(headers)).thenReturn(user);

		final var cardDto = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.user(user)
				.build();
		when(cardRepository.save(cardDto)).thenReturn(cardDto);

		final var cardResponse = CardResponse.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO.getValue())
				.build();
		when(cardAssembler.toModel(cardDto)).thenReturn(cardResponse);

		final var response = cardService.createCard(headers, request);
		assertEquals(cardResponse, response);
	}

	@Test
	void updateCard_cardExists() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;
		final var request = UpdateCardRequest.builder()
				.name("name")
				.description("Description")
				.color("#ff1111")
				.status(Status.IN_PROGRESS)
				.build();

		final var cardDto = CardDto.builder()
				.name("name 1")
				.description("description 1")
				.color("#ff0000")
				.creationDate(LocalDate.now())
				.status(Status.TO_DO)
				.build();
		when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardDto));

		final var cardDtoUpdate = CardDto.builder()
				.name("name")
				.description("Description")
				.color("#ff1111")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS)
				.build();
		when(cardRepository.save(cardDtoUpdate)).thenReturn(cardDtoUpdate);

		final var cardResponse = CardResponse.builder()
				.name("name")
				.description("Description")
				.color("#ff1111")
				.creationDate(LocalDate.now())
				.status(Status.IN_PROGRESS.getValue())
				.build();
		when(cardAssembler.toModel(cardDto)).thenReturn(cardResponse);

		final var response = cardService.updateCard(headers, cardId, request);
		assertEquals(cardResponse, response);

		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
	}

	@Test
	void updateCard_cardNotExists_returnNull() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;
		final var request = UpdateCardRequest.builder()
				.name("name")
				.build();

		when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

		assertNull(cardService.updateCard(headers, cardId, request));
		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
		verify(cardRepository, times(0)).save(any());
	}

	@Test
	void deleteCard_cardExists_returnTrue() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;

		when(cardRepository.existsById(cardId)).thenReturn(true);

		assertTrue(cardService.deleteCard(headers, cardId));

		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
		verify(cardRepository, times(1)).deleteById(cardId);
	}

	@Test
	void deleteCard_cardNotExists_returnFalse() {

		final var headers = new HttpHeaders();
		final var cardId = 1L;

		when(cardRepository.existsById(cardId)).thenReturn(false);

		assertFalse(cardService.deleteCard(headers, cardId));

		verify(authorizationService).checkAuthorizationOfAction(headers, cardId);
		verify(cardRepository, times(0)).deleteById(cardId);
	}
}