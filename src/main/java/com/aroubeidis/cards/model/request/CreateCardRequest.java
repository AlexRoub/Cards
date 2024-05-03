package com.aroubeidis.cards.model.request;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Builder
public class CreateCardRequest {

	private String name;
	@Nullable
	private String description;
	@Nullable
	private String color;
}
