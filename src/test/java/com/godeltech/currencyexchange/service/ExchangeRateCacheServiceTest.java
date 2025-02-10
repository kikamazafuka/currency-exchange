package com.godeltech.currencyexchange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.exception.CurrencyNotValidException;
import com.godeltech.currencyexchange.exception.NotFoundException;
import com.godeltech.currencyexchange.validator.CurrencyValidator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeRateCacheServiceTest {

  @Mock private Map<String, Map<String, Double>> exchangeRatesBean;

  @Mock private CurrencyValidator currencyValidator;

  @InjectMocks private ExchangeRateCacheService exchangeRateCacheService;

  private String currencyCode;

  @BeforeEach
  public void setup() {
    currencyCode = "EUR";
  }

  @Test
  void getCurrencyCacheExchangeRates() {

    final var amount = 100.0;
    final var exchangeRates = Map.of("USD", 1.2, "GBP", 0.8);
    final var expectedRates = Map.of("USD", 120.0, "GBP", 80.0);

    when(currencyValidator.isCurrencyValid(currencyCode)).thenReturn(true);
    when(exchangeRatesBean.get(currencyCode)).thenReturn(exchangeRates);
    when(exchangeRateCacheService.getCurrencyCacheExchangeRates(currencyCode, amount))
        .thenReturn(exchangeRates);

    final var actualRates =
        exchangeRateCacheService.getCurrencyCacheExchangeRates(currencyCode, amount);

    assertEquals(expectedRates, actualRates);
  }

  @Test
  void getCurrencyCacheExchangeRates_exchangeRatesNotFound() {

    final var amount = 100.0;
    final var currencyWithoutExchangeRates = "BYN";

    when(currencyValidator.isCurrencyValid(currencyWithoutExchangeRates)).thenReturn(true);
    when(exchangeRatesBean.get(currencyWithoutExchangeRates)).thenReturn(null);

    final var exception =
        assertThrows(
            NotFoundException.class,
            () ->
                exchangeRateCacheService.getCurrencyCacheExchangeRates(
                    currencyWithoutExchangeRates, amount));

    assertEquals("Exchange rate for BYN not found.", exception.getMessage());
  }

  @Test
  void getCurrencyCacheExchangeRates_currencyNotValidException() {

    final var invalidCurrencyCode = "UUU";
    final var amount = 100.0;

    CurrencyNotValidException exception =
        assertThrows(
            CurrencyNotValidException.class,
            () ->
                exchangeRateCacheService.getCurrencyCacheExchangeRates(
                    invalidCurrencyCode, amount));

    assertEquals("Currency with such currency code doesn't exists", exception.getMessage());
  }
}
