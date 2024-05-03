package com.aroubeidis.cards.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class Utils {

	public static Sort.Direction getSortDirection(final String direction) {

		return direction.contains("desc")
			? Sort.Direction.DESC
			: Sort.Direction.ASC;
	}
}
