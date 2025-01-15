package com.godeltech.currencyexchange.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.service.CurrencyService;
import java.util.List;
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

  @BeforeEach
  public void setup() {
    eur = Currency.builder().currencyCode("EUR").build();
    usd = Currency.builder().currencyCode("USD").build();
    eurDto = new CurrencyDto("EUR");
    usdDto = new CurrencyDto("USD");
  }

  @Test
  void getCurrencies() throws Exception {

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
}
