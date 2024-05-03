package com.aroubeidis.cards.model;

import org.springframework.http.HttpHeaders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCardsVO {

	private HttpHeaders headers;
	private int page;
	private int size;
	private String[] sort;
	private RequestFilters filters;
}
