package com.aroubeidis.cards.model;

import java.time.LocalDateTime;

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
	private LocalDateTime creationDate;
}
