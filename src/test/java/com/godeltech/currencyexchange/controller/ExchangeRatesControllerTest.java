package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.currencyexchange.exception.NotFoundException;
import com.godeltech.currencyexchange.service.ExchangeRateCacheService;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExchangeRatesController.class)
class ExchangeRatesControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ExchangeRateCacheService exchangeRateCacheService;

  private static final String CURRENCIES_ENDPOINT = "/api/v1";
  private static final String EXCHANGE_RATES_ENDPOINT = "/exchange-rates";

  private String currencyCode;

  @BeforeEach
  public void setup() {
    currencyCode = "USD";
  }

  @Test
  @SneakyThrows
  void getExchangeRate() {

    final var amount = 100.0;

    when(exchangeRateCacheService.getCurrencyCacheExchangeRates(currencyCode, amount))
        .thenReturn(Map.of("EUR", 1.2 * amount, "GBP", 0.8 * amount));

    final var result =
        mockMvc
            .perform(
                get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
                    .param("currency", currencyCode)
                    .param("amount", String.valueOf(amount))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final var responseBody = result.getResponse().getContentAsString();

    final var expectedBody =
        new String(Files.readAllBytes(Paths.get("src/test/resources/expected_body_amount.json")));
    final var objectMapper = new ObjectMapper();
    final var expectedJson = objectMapper.readTree(expectedBody);
    final var responseJson = objectMapper.readTree(responseBody);

    assertEquals(expectedJson, responseJson);

    verify(exchangeRateCacheService).getCurrencyCacheExchangeRates(currencyCode, amount);
  }

  @Test
  @SneakyThrows
  void getExchangeRate_emptyCurrencyCode() {

    final var amount = 100.0;

    mockMvc
        .perform(
            get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
                .param("currency", "")
                .param("amount", String.valueOf(amount)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$")
                .value(
                    hasItems(
                        "Currency code must not be blank",
                        "Currency code must be a 3-letter uppercase string")));

    verify(exchangeRateCacheService, times(0)).getCurrencyCacheExchangeRates(currencyCode, amount);
  }

  @Test
  void getExchangeRate_amountZero() throws Exception {

    final var amount = 0.0;

    when(exchangeRateCacheService.getCurrencyCacheExchangeRates(currencyCode, amount))
        .thenReturn(Map.of("EUR", 1.2, "GBP", 0.8));

    final var result =
        mockMvc
            .perform(
                get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
                    .param("currency", currencyCode)
                    .param("amount", String.valueOf(amount))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final var responseBody = result.getResponse().getContentAsString();
    final var expectedBody =
        new String(
            Files.readAllBytes(Paths.get("src/test/resources/expected_body_amount_zero.json")));
    final var objectMapper = new ObjectMapper();
    final var expectedJson = objectMapper.readTree(expectedBody);
    final var responseJson = objectMapper.readTree(responseBody);

    assertEquals(expectedJson, responseJson);

    verify(exchangeRateCacheService).getCurrencyCacheExchangeRates(currencyCode, amount);
  }

  @Test
  @SneakyThrows
  void getCurrencyCacheExchangeRates_exchangeRatesNotFound() {

    final var notValidCurrencyCode = "BYN";
    final var amount = 1.0;

    final var exception =
        new NotFoundException(
            "Exchange rate for " + notValidCurrencyCode + " not found.");

    when(exchangeRateCacheService.getCurrencyCacheExchangeRates(notValidCurrencyCode, amount))
        .thenThrow(exception);

    mockMvc
        .perform(
            get(CURRENCIES_ENDPOINT + EXCHANGE_RATES_ENDPOINT)
                .param("currency", notValidCurrencyCode)
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.message")
                .value("Exchange rate for " + notValidCurrencyCode + " not found."));
  }
}
