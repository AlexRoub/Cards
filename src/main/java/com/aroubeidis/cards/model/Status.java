package com.aroubeidis.cards.model;

import java.util.Arrays;

import com.aroubeidis.cards.exceptions.BadRequestException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {

	TO_DO("To Do"),
	IN_PROGRESS("In Progress"),
	DONE("Done");

	private final String value;

	public static Status findByValue(final String val)
			throws Exception {

		return Arrays.stream(Status.values())
				.filter(v -> v.value.equals(val))
				.findFirst()
				.orElseThrow(() -> BadRequestException.builder()
						.message(String.format("Unknown Status value: '%s'", val))
						.build());
	}
}