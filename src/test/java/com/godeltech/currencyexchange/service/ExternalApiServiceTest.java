package com.godeltech.currencyexchange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.provider.ExchangeRateProvider;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

  @Mock private Map<String, Map<String, Double>> exchangeRatesBean;

  @Mock private ExchangeRateProvider provider;

  @InjectMocks private ExternalApiService externalApiService;

  @BeforeEach
  public void setUp() {
    externalApiService = new ExternalApiService(exchangeRatesBean, List.of(provider));
  }

  @Test
  public void updateExchangeRates() {

    final var response = new ExternalApiResponse("USD", new Date().toString(), Map.of("EUR", 0.85));

    when(provider.getExchangeRates()).thenReturn(List.of(response));

    externalApiService.updateExchangeRates();

    verify(exchangeRatesBean).putAll(anyMap());
    verify(provider).getExchangeRates();
  }

  @Test
  public void updateExchangeRates_throwsException() {

    when(provider.getExchangeRates()).thenThrow(new ResourceAccessException("API unavailable"));

    externalApiService.updateExchangeRates();

    final var exception =
        assertThrows(ResourceAccessException.class, () -> provider.getExchangeRates());

    assertEquals("API unavailable", exception.getMessage());

    verify(exchangeRatesBean).putAll(anyMap());
  }

  @Test
  public void updateExchangeRatesTest() {

    final var response = new ExternalApiResponse("USD", new Date().toString(), Map.of("EUR", 0.85));

    when(provider.getExchangeRates()).thenReturn(List.of(response));

    externalApiService.updateExchangeRates();

    verify(exchangeRatesBean).putAll(anyMap());
    verify(provider).getExchangeRates();
  }
}
