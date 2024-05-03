package com.aroubeidis.cards.model.response;

import java.time.LocalDateTime;

import com.aroubeidis.cards.model.Status;
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

	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("color")
	private String color;
	@JsonProperty("status")
	private Status status;
	@JsonProperty("creationDate")
	private LocalDateTime creationDate;
}
