package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.hasItems;

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

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
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
    RestAssured.baseURI = "http://localhost:" + port;
    currencyRepository.deleteAll();
  }

  @Test
  void shouldGetAllCurrencies() {
    List<Currency> currencies =
        List.of(
            Currency.builder().currencyCode("USD").build(),
            Currency.builder().currencyCode("EUR").build());
    currencyRepository.saveAll(currencies);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/v1/currencies")
        .then()
        .statusCode(200)
        .body("currencyCode", hasItems("USD", "EUR"));
  }
}
