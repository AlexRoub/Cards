package com.aroubeidis.cards.model.request;

import jakarta.validation.constraints.Pattern;
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
public class UpdateCardRequest {

	private String name;
	private String description;
	@Pattern(regexp = "^#[a-zA-Z0-9]{6}$")
	private String color;
	private String status;
}
