package com.godeltech.currencyexchange.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.godeltech.currencyexchange.dto.CurrencyDto;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CurrencyService currencyService;

  @MockitoBean private CurrencyMapper currencyMapper;

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
  void getCurrencies() {

    when(currencyService.getAllCurrencies()).thenReturn(List.of(usd, eur));

    when(currencyMapper.currenciesToCurrencyDtos(List.of(usd, eur)))
        .thenReturn(List.of(usdDto, eurDto));

    mockMvc
        .perform(get("/api/v1/currencies").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].currencyCode").value("USD"))
        .andExpect(jsonPath("$[1].currencyCode").value("EUR"));
  }

  @Test
  @SneakyThrows
  void addCurrency_success() {

    when(currencyService.existsByCurrencyCode(currencyCode)).thenReturn(false);
    when(currencyService.addCurrency(usd)).thenReturn(usd);
    when(currencyMapper.currencyToCurrencyDto(usd)).thenReturn(usdDto);

    mockMvc
        .perform(post("/api/v1/currencies").param("currency", currencyCode))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.currencyCode").value("USD"));

    verify(currencyService, times(1)).addCurrency(usd);
  }

  @Test
  @SneakyThrows
  void addCurrency_invalidCurrencyCode() {

    mockMvc
        .perform(post("/api/v1/currencies").param("currency", "us"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]").value("Currency code must be a 3-letter uppercase string"));

    verify(currencyService, times(0)).addCurrency(any(Currency.class));
  }

  @Test
  @SneakyThrows
  void addCurrency_emptyCurrencyCode() {

    mockMvc
        .perform(post("/api/v1/currencies").param("currency", ""))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$")
                .value(
                    hasItems(
                        "Currency code must not be blank",
                        "Currency code must be a 3-letter uppercase string")));

    verify(currencyService, times(0)).addCurrency(any(Currency.class));
  }

  @Test
  @SneakyThrows
  void addCurrency_currencyAlreadyExists() {

    when(currencyService.existsByCurrencyCode(currencyCode)).thenReturn(true);
    when(currencyMapper.currencyToCurrencyDto(usd)).thenReturn(usdDto);

    mockMvc
        .perform(post("/api/v1/currencies").param("currency", currencyCode))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Currency with this code already exists"));
    verify(currencyService, times(0)).addCurrency(any(Currency.class));
  }
}
