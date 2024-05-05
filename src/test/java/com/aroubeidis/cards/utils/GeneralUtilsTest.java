package com.aroubeidis.cards.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.aroubeidis.cards.exceptions.BadRequestException;

@ExtendWith(MockitoExtension.class)
class GeneralUtilsTest {

	@Test
	void getSort_sortByValueExists_desc_success() {

		final var response = GeneralUtils.getSort("name", "desc");

		final var expectedResponse = Sort.by(Sort.Direction.DESC, "name");

		assertEquals(expectedResponse, response);
	}

	@Test
	void getSort_sortByValueExists_asc_success() {

		final var response = GeneralUtils.getSort("name", "asc");

		final var expectedResponse = Sort.by(Sort.Direction.ASC, "name");

		assertEquals(expectedResponse, response);
	}

	@Test
	void getSort_sortByValue_notExists_throwsBadRequestException() {

		assertThrows(BadRequestException.class, () -> GeneralUtils.getSort("description", "desc"), "Sort value provided is not valid");
	}
}