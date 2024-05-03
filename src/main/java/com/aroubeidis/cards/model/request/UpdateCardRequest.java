package com.aroubeidis.cards.model.request;

import org.springframework.lang.Nullable;

import com.aroubeidis.cards.model.Status;

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
	@Nullable
	private String description;
	@Nullable
	private String color;
	@Nullable
	private Status status;
}
