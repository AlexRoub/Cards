package com.aroubeidis.cards.service;

import static com.aroubeidis.cards.utils.Utils.getSortDirection;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aroubeidis.cards.assembler.CardAssembler;
import com.aroubeidis.cards.dto.CardDto;
import com.aroubeidis.cards.model.GetCardsVO;
import com.aroubeidis.cards.model.SortEnum;
import com.aroubeidis.cards.model.Status;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.aroubeidis.cards.model.response.CardResponse;
import com.aroubeidis.cards.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

	private static final Pattern COLOR_PATTERN = Pattern.compile("^#[a-zA-Z0-9]{6}$");

	private final CardRepository cardRepository;
	private final CardAssembler cardAssembler;

	public Page<CardDto> getAllCards(@NonNull final GetCardsVO cardsVO)
		throws BadRequestException {

		final var sortBy = getSort(cardsVO.getSort()[0]);
		final var page = PageRequest.of(cardsVO.getPage(), cardsVO.getSize(), sortBy);

		return cardRepository.findAll(page);
	}

	public CardResponse getCardById(final Long id) {

		final var cardDto = cardRepository.findById(id)
			.orElse(null);

		return cardAssembler.toModel(cardDto);
	}

	public CardResponse createCard(final CreateCardRequest createCardRequest)
		throws BadRequestException {

		final var color = createCardRequest.getColor();
		final var isColorFormatValid = Optional.ofNullable(createCardRequest.getColor())
			.map(COLOR_PATTERN::matcher)
			.map(Matcher::matches)//TODO check if we need matches or find
			.orElse(false);

		if (!isColorFormatValid) {
			throw new BadRequestException("Color format provided is not valid");
		}

		final var cardDto = CardDto.builder()
			.name(createCardRequest.getName())
			.description(createCardRequest.getDescription())
			.color(color)
			.status(Status.TO_DO)
			.build();

		final var createdCard = cardRepository.save(cardDto);

		return cardAssembler.toModel(createdCard);
	}

	public CardResponse updateCard(final Long id, @NonNull final UpdateCardRequest request) {

		final var cardDto = cardRepository.findById(id)
			.orElse(null);

		if (cardDto == null) {
			return null;
		}

		cardDto.setName(request.getName());
		cardDto.setDescription(request.getDescription()); //TODO check during test if it can br cleared out
		cardDto.setColor(request.getColor());
		cardDto.setStatus(request.getStatus());

		final var card = cardRepository.save(cardDto);

		return cardAssembler.toModel(card);
	}

	public boolean deleteCard(final Long id) {

		if (cardRepository.existsById(id)) {
			cardRepository.deleteById(id);
			return true;
		}

		return false;
	}

	private Sort getSort(final String sort)
		throws BadRequestException {

		final var splitSort = sort.split(",");

		final var sortValue = splitSort[0];
		final var isSortValueValid = Stream.of(SortEnum.values())
			.map(SortEnum::getValue)
			.anyMatch(sortValue::contains);

		if (!isSortValueValid) {
			throw new BadRequestException("Sort value provided is not valid");
		}

		return Sort.by(getSortDirection(splitSort[1]), sortValue);
	}
}

