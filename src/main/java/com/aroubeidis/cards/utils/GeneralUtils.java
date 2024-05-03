package com.aroubeidis.cards.utils;

import java.util.stream.Stream;

import org.springframework.data.domain.Sort;

import com.aroubeidis.cards.exceptions.BadRequestException;
import com.aroubeidis.cards.model.SortEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeneralUtils {

	public static Sort getSort(final String sort, final String direction)
		throws BadRequestException {

		Stream.of(SortEnum.values())
			.map(SortEnum::getValue)
			.filter(sort::contains)
			.findAny()
			.orElseThrow(() -> BadRequestException.builder()
				.message("Sort value provided is not valid")
				.build());

		return Sort.by(getSortDirection(direction), sort);
	}

	private static Sort.Direction getSortDirection(final String direction) {

		return direction.contains("desc")
			? Sort.Direction.DESC
			: Sort.Direction.ASC;
	}
}
