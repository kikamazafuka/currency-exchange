package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.godeltech.currencyexchange.JsonFormatter;
import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.exception.EntityAlreadyExistsException;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.service.CurrencyService;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CurrencyService currencyService;

  @MockitoBean private CurrencyMapper currencyMapper;

  private static final String CURRENCIES_ENDPOINT = "/api/v1/currencies";

  private Currency eur;
  private Currency usd;
  private CurrencyDto eurDto;
  private CurrencyDto usdDto;
  private String currencyCode;

  @BeforeEach
  public void setup() {
    eur = Currency.builder().currencyCode("EUR").build();
    usd = Currency.builder().currencyCode("USD").build();
    eurDto = new CurrencyDto("EUR");
    usdDto = new CurrencyDto("USD");
    currencyCode = "USD";
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void getCurrencies() {

    when(currencyService.getAllCurrencies()).thenReturn(List.of(usd, eur));
    when(currencyMapper.currenciesToCurrencyDtos(List.of(usd, eur)))
        .thenReturn(List.of(usdDto, eurDto));

    final var result =
        mockMvc
            .perform(get(CURRENCIES_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    final var responseBody = result.getResponse().getContentAsString();
    final var expectedBody =
        JsonFormatter.transformJsonFormat("src/test/resources/expected_currencies.json");

    assertEquals(expectedBody, responseBody);

    verify(currencyService).getAllCurrencies();
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void addCurrency_success() {

    when(currencyService.existsByCurrencyCode(currencyCode)).thenReturn(false);
    when(currencyService.addCurrency(currencyCode)).thenReturn(usdDto);
    when(currencyMapper.currencyToCurrencyDto(usd)).thenReturn(usdDto);

    final var result =
        mockMvc
            .perform(post(CURRENCIES_ENDPOINT).param("currency", currencyCode).with(csrf()))
            .andExpect(status().isCreated())
            .andReturn();

    final var responseBody = result.getResponse().getContentAsString();
    final var expectedBody =
        JsonFormatter.transformJsonFormat("src/test/resources/expected_body.json");

    assertEquals(expectedBody, responseBody);

    verify(currencyService).addCurrency(currencyCode);
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void addCurrency_invalidCurrencyCode() {

    mockMvc
        .perform(post(CURRENCIES_ENDPOINT).param("currency", "us").with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]").value("Currency code must be a 3-letter uppercase string"));

    verify(currencyService, times(0)).addCurrency(currencyCode);
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void addCurrency_emptyCurrencyCode() {

    mockMvc
        .perform(post(CURRENCIES_ENDPOINT).param("currency", "").with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$")
                .value(
                    hasItems(
                        "Currency code must not be blank",
                        "Currency code must be a 3-letter uppercase string")));

    verify(currencyService, times(0)).addCurrency(currencyCode);
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void addCurrency_currencyAlreadyExists() {

    final var exception =
        new EntityAlreadyExistsException("Currency with this code already exists");

    when(currencyService.addCurrency(currencyCode)).thenThrow(exception);

    mockMvc
        .perform(post(CURRENCIES_ENDPOINT).param("currency", currencyCode).with(csrf()))
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Currency with this code already exists"));
  }
}
