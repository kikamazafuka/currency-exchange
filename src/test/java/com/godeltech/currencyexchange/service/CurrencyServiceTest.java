package com.godeltech.currencyexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock private CurrencyRepository currencyRepository;

  @InjectMocks private CurrencyService currencyService;

  private Currency eur;
  private Currency usd;
  private String currencyCode;

  @BeforeEach
  public void setup() {
    eur = Currency.builder().id(1L).currencyCode("EUR").build();
    usd = Currency.builder().id(2L).currencyCode("USD").build();
    currencyCode = "USD";
  }

  @Test
  void getAllCurrencies() {

    when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

    final var currencies = currencyService.getAllCurrencies();

    assertThat(currencies).isNotNull();
    assertThat(currencies).containsExactly(usd, eur);
  }

  @Test
  void addCurrency() {
    when(currencyRepository.save(usd)).thenReturn(usd);

    final var usdActual = currencyService.addCurrency(usd);
    final var usdExpected = Currency.builder().id(2L).currencyCode("USD").build();

    assertThat(usdExpected).isNotNull();
    assertEquals(usdExpected, usdActual);
  }

  @Test
  void existsByCurrencyCode_returnTrue() {

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(true);

    final var result = currencyService.existsByCurrencyCode(currencyCode);
    assertTrue(result);
    verify(currencyRepository, times(1)).existsByCurrencyCode(currencyCode);
  }

  @Test
  void existsByCurrencyCode_returnFalse() {

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(false);

    final var result = currencyService.existsByCurrencyCode(currencyCode);
    assertFalse(result);
    verify(currencyRepository, times(1)).existsByCurrencyCode(currencyCode);
  }
}
