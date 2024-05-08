package com.aroubeidis.cards.service;

import static com.aroubeidis.cards.utils.GeneralUtils.getSort;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aroubeidis.cards.assembler.CardAssembler;
import com.aroubeidis.cards.entities.CardDto;
import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.Role;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.repository.CardRepository;
import com.aroubeidis.cards.specifications.CardSpecifications;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

	private final CardRepository cardRepository;
	private final CardAssembler cardAssembler;
	private final AuthorizationService authorizationService;

	public Page<CardResponse> getAllCards(@NonNull final GetCardsVO cardsVO) {

		final var user = authorizationService.getUser(cardsVO.getHeaders());

		final var sortBy = getSort(cardsVO.getSort()[0], cardsVO.getSort()[1]);
		final var page = PageRequest.of(cardsVO.getPage(), cardsVO.getSize(), sortBy);

		final var filters = cardsVO.getFilters();
		final var userId = user.getRole() == Role.ADMIN
				? null
				: user.getId();

		final var spec = CardSpecifications.createSpecification(filters, userId);
		final var cardPage = cardRepository.findAll(spec, page);

		return cardAssembler.toModelPage(cardPage);
	}

	public CardResponse getCardById(final HttpHeaders headers, final Long cardId) {

		final var cardDto = authorizationService.getCardAfterAuthorization(headers, cardId);

		return cardAssembler.toModel(cardDto);
	}

	public CardResponse createCard(final HttpHeaders headers, final CreateCardRequest createCardRequest) {

		final var user = authorizationService.getUser(headers);

		final var cardDto = CardDto.builder()
				.name(createCardRequest.getName())
				.description(createCardRequest.getDescription())
				.color(createCardRequest.getColor())
				.status(Status.TO_DO)
				.creationDate(LocalDate.now())
				.user(user)
				.build();

		final var createdCard = cardRepository.save(cardDto);

		return cardAssembler.toModel(createdCard);
	}

	public CardResponse updateCard(final HttpHeaders headers, final Long cardId, @NonNull final UpdateCardRequest request)
			throws Exception {

		final var cardDto = authorizationService.getCardAfterAuthorization(headers, cardId);

		if (cardDto == null) {
			return null;
		}

		if (StringUtils.isNotBlank(request.getName())) {
			cardDto.setName(request.getName());
		}
		if (request.getDescription() != null) {
			cardDto.setDescription(request.getDescription());
		}
		if (request.getColor() != null) {
			cardDto.setColor(request.getColor());
		}
		if (request.getStatus() != null) {
			cardDto.setStatus(Status.findByValue(request.getStatus()));
		}

		final var card = cardRepository.save(cardDto);

		return cardAssembler.toModel(card);
	}

	public boolean deleteCard(final HttpHeaders headers, final Long cardId) {

		final var cardDto = authorizationService.getCardAfterAuthorization(headers, cardId);

		if (cardDto != null) {
			cardRepository.deleteById(cardId);
			return true;
		}

		return false;
	}
}

