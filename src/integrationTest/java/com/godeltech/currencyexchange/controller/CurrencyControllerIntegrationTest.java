package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.godeltech.currencyexchange.JsonFormatter;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyControllerIntegrationTest {

  @LocalServerPort private Integer port;

  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine");
  private static final String BASE_URL = "http://localhost:";
  private static final String CURRENCIES_ENDPOINT = "/api/v1/currencies";
  private Currency eur;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired CurrencyRepository currencyRepository;

  @BeforeEach
  void setUp() {
    RestAssured.reset();
    currencyRepository.deleteAll();
    eur = Currency.builder().currencyCode("EUR").build();
  }

  @Test
  void shouldGetAllCurrencies() {
    final var currencies =
        List.of(
            Currency.builder().currencyCode("USD").build(),
            Currency.builder().currencyCode("EUR").build());
    currencyRepository.saveAll(currencies);

    final var responseBody =
        RestAssured.given()
            .baseUri(BASE_URL + port)
            .contentType(ContentType.JSON)
            .when()
            .get(CURRENCIES_ENDPOINT)
            .then()
            .statusCode(200)
            .body("currencyCode", hasItems("USD", "EUR"))
            .extract()
            .body()
            .asString();

    final var expectedBody =
        JsonFormatter.transformJsonFormat("src/integrationTest/resources/expected_currencies.json");

    assertEquals(expectedBody, responseBody);
  }

  @Test
  void shouldAddCurrency() {

    final var validCurrency = "USD";

    RestAssured.given()
        .baseUri(BASE_URL + port)
        .contentType(ContentType.JSON)
        .queryParam("currency", validCurrency)
        .when()
        .post(CURRENCIES_ENDPOINT)
        .then()
        .statusCode(201)
        .body("currencyCode", equalTo(validCurrency));
  }

  @Test
  void shouldAddCurrency_currencyExists() {

    final var validCurrency = "EUR";
    currencyRepository.save(eur);

    RestAssured.given()
        .baseUri(BASE_URL + port)
        .contentType(ContentType.JSON)
        .queryParam("currency", validCurrency)
        .when()
        .post(CURRENCIES_ENDPOINT)
        .then()
        .statusCode(409)
        .body("message", equalTo("Currency with this code already exists"));
  }
}
