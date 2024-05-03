package com.aroubeidis.cards.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {

	TO_DO("To Do"),
	IN_PROGRESS("In Progress"),
	DONE("Done");

	private final String value;
}