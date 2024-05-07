package com.aroubeidis.cards.IT;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup(@Sql(scripts = { "/__files/db/schema.sql", "/__files/db/data.sql" }))
public abstract class AbstractIT {

	private static final int WIREMOCK_PORT = 8081;
	private WireMockServer wireMockServer;
	@Autowired
	protected MockMvc mockMvc;
	protected ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {

		wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
				.port(WIREMOCK_PORT));
		wireMockServer.start();
		objectMapper = new ObjectMapper();
	}

	@AfterEach
	public void tearDown() {

		wireMockServer.stop();
	}
}
