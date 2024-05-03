package com.aroubeidis.cards.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SortEnum {

	NAME("name"),
	COLOR("color"),
	STATUS("status"),
	CREATION_DATE("creationDate");

	private final String value;
}
