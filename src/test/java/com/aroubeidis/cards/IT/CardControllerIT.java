package com.aroubeidis.cards.IT;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.request.CreateCardRequest;
import com.aroubeidis.cards.model.request.UpdateCardRequest;
import com.github.tomakehurst.wiremock.common.Json;

public class CardControllerIT extends AbstractIT {

	@Test
	public void getCards_MEMBER()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		final var filters = RequestFilters.builder()
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(filters)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].id").value("3"))
				.andExpect(jsonPath("$.content[0].name").value("Card 3"))
				.andExpect(jsonPath("$.content[0].description").value("Description 3"))
				.andExpect(jsonPath("$.content[0].color").value("#ff0000"))
				.andExpect(jsonPath("$.content[0].status").value("To Do"))
				.andExpect(jsonPath("$.content[0].creationDate").value("2024-05-04"));
	}

	@Test
	public void getCards_ADMIN_defaultSortCreationDateDESC()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiQGMuY29tIiwiaWF0IjoxNzE1MDg2NTQ0LCJleHAiOjE3MTUxNzI5NDR9"
						+ ".xwOIRvsC48oePk41T5JIxZR1z-eWhMG1stVEsO1ElTg");

		final var filters = RequestFilters.builder()
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(filters)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value("4"))
				.andExpect(jsonPath("$.content[0].name").value("Card 4"))
				.andExpect(jsonPath("$.content[0].description").value("Description 4"))
				.andExpect(jsonPath("$.content[0].color").value("#00ff00"))
				.andExpect(jsonPath("$.content[0].status").value("In Progress"))
				.andExpect(jsonPath("$.content[0].creationDate").value("2024-05-05"))
				.andExpect(jsonPath("$.content[1].id").value("3"))
				.andExpect(jsonPath("$.content[1].name").value("Card 3"))
				.andExpect(jsonPath("$.content[1].description").value("Description 3"))
				.andExpect(jsonPath("$.content[1].color").value("#ff0000"))
				.andExpect(jsonPath("$.content[1].status").value("To Do"))
				.andExpect(jsonPath("$.content[1].creationDate").value("2024-05-04"));
	}

	@Test
	public void getCardById()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/3")
						.headers(headers))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("3"))
				.andExpect(jsonPath("$.name").value("Card 3"))
				.andExpect(jsonPath("$.description").value("Description 3"))
				.andExpect(jsonPath("$.color").value("#ff0000"))
				.andExpect(jsonPath("$.status").value("To Do"))
				.andExpect(jsonPath("$.creationDate").value("2024-05-04"));
	}

	@Test
	public void getCardById_cardNotFound()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/5")
						.headers(headers))
				.andExpect(status().isNotFound());
	}

	@Test
	public void createCard()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		final var createCardRequest = CreateCardRequest.builder()
				.name("Card 1")
				.description("Description 1")
				.color("#0000ff")
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/create")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(createCardRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.name").value("Card 1"))
				.andExpect(jsonPath("$.description").value("Description 1"))
				.andExpect(jsonPath("$.color").value("#0000ff"))
				.andExpect(jsonPath("$.status").value("To Do"))
				.andExpect(jsonPath("$.creationDate").isNotEmpty());
	}

	@Test
	public void updateCard_cardExists()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		final var updateCardRequest = UpdateCardRequest.builder()
				.name("Updated Card 3")
				.build();

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/cards/3")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(updateCardRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("3"))
				.andExpect(jsonPath("$.name").value("Updated Card 3"))
				.andExpect(jsonPath("$.description").value("Description 3"))
				.andExpect(jsonPath("$.color").value("#ff0000"))
				.andExpect(jsonPath("$.status").value("To Do"))
				.andExpect(jsonPath("$.creationDate").value("2024-05-04"));
	}

	@Test
	public void updateCard_cardNotFound()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		final var updateCardRequest = UpdateCardRequest.builder()
				.name("Updated Card 1")
				.build();

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/cards/1")
						.headers(headers)
						.contentType(MediaType.APPLICATION_JSON)
						.content(Json.toByteArray(updateCardRequest)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void deleteCard_cardExists()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/3")
						.headers(headers))
				.andExpect(status().isNoContent());
	}

	@Test
	public void deleteCard_cardNotFound()
			throws Exception {

		final var headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGIuY29tIiwiaWF0IjoxNzE1MDg0ODIyLCJleHAiOjE3MTUxNzEyMjJ9"
						+ ".3CtcWMG68bTAuFvnPX9WoSAYVAFCEeBQW-F1cl6_kC8");

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/5")
						.headers(headers))
				.andExpect(status().isNotFound());
	}
}
