package com.aroubeidis.cards.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilters {

	private String name;
	private String color;
	private String status;
	private LocalDate creationDate;
}
