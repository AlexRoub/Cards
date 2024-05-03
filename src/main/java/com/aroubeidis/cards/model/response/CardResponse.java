package com.aroubeidis.cards.model.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class CardResponse {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("color")
	private String color;
	@JsonProperty("status")
	private String status;
	@JsonProperty("creationDate")
	private LocalDate creationDate;
}
