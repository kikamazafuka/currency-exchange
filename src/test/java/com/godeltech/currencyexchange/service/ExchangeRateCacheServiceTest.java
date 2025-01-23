package com.godeltech.currencyexchange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.exception.NotFoundException;
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

  @InjectMocks private ExchangeRateCacheService exchangeRateCacheService;

  private String currencyCode;

  @BeforeEach
  public void setup() {
    currencyCode = "USD";
  }

  @Test
  void getCurrencyCacheExchangeRates() {

    final var amount = 100.0;
    final var exchangeRates = Map.of("EUR", 1.2, "GBP", 0.8);
    final var expectedRates = Map.of("EUR", 120.0, "GBP", 80.0);

    when(exchangeRateCacheService.getExchangeRates(currencyCode)).thenReturn(exchangeRates);

    final var actualRates =
        exchangeRateCacheService.getCurrencyCacheExchangeRates(currencyCode, amount);

    assertEquals(expectedRates, actualRates);
  }

  @Test
  void getCurrencyCacheExchangeRates_exchangeRatesNotFound() {

    final var currencyWithoutExchangeRates = "BYN";

    when(exchangeRateCacheService.getExchangeRates(currencyWithoutExchangeRates)).thenReturn(null);

    assertThrows(
        NotFoundException.class,
        () ->
            exchangeRateCacheService.getCurrencyCacheExchangeRates(
                currencyWithoutExchangeRates, 100.0));
  }
}
