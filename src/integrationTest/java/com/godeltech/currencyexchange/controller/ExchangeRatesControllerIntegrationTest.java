package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
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
public class ExchangeRatesControllerIntegrationTest {

  @LocalServerPort private Integer port;

  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine");
  private static final String BASE_URL = "http://localhost:";
  private static final String CURRENCIES_ENDPOINT = "/api/v1";
  private static final String EXCHANGE_RATES_ENDPOINT = "/exchange-rates";

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

  @Autowired Map<String, Map<String, Double>> exchangeRates;

  @BeforeEach
  void setUp() {
    RestAssured.reset();
  }

  @Test
  @SneakyThrows
  void shouldGetCurrencyExchangeRates() {
    Map<String, Double> eurRates = new HashMap<>();
    eurRates.put("USD", 2.0417);
    eurRates.put("CAD", 1.2600);
    eurRates.put("GBP", 0.7191);
    exchangeRates.put("EUR", eurRates);

    final var amount = 100.0;
    final var currencyCode = "EUR";

    final var responseBody =
        RestAssured.given()
            .baseUri(BASE_URL + port)
            .contentType(ContentType.JSON)
            .queryParam("currency", currencyCode)
            .queryParam("amount", amount)
            .when()
            .get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    final var expectedBody =
        new String(
            Files.readAllBytes(Paths.get("src/integrationTest/resources/expected_body.json")));
    final var objectMapper = new ObjectMapper();
    final var expectedJson = objectMapper.readTree(expectedBody);
    final var responseJson = objectMapper.readTree(responseBody);

    assertEquals(expectedJson, responseJson);
  }

  @Test
  void shouldGetCurrencyExchangeRates_currencyNotFound() {

    final var currencyCode = "BYN";
    final var amount = 100.0;

    RestAssured.given()
        .baseUri(BASE_URL + port)
        .contentType(ContentType.JSON)
        .queryParam("currency", currencyCode)
        .queryParam("amount", amount)
        .when()
        .get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
        .then()
        .statusCode(404)
        .body("message", equalTo("Exchange rate for " + currencyCode + " not found."));
  }
}
