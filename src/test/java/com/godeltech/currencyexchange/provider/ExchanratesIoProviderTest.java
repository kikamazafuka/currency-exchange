package com.godeltech.currencyexchange.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.mapper.ApiResponseMapper;
import com.godeltech.currencyexchange.provider.response.ExchangeratesIoApiResponse;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import com.godeltech.currencyexchange.service.ApiRequestLogService;
import com.godeltech.currencyexchange.service.CurrencyService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExchanratesIoProviderTest {

  @Mock private RestTemplate restTemplate;

  @Mock private ApiRequestLogService apiRequestLogService;

  @Mock private ApiResponseMapper apiResponseMapper;

  @Mock private CurrencyService currencyService;

  @InjectMocks private ExchangeratesIoProvider exchangeratesIoProvider;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
            exchangeratesIoProvider, "apiUrl", "http://localhost:8080/api/v1/local-rates");
    ReflectionTestUtils.setField(exchangeratesIoProvider, "apiKey", "test");
  }

  @Test
  void getExchangeRates_successfulResponse() {

    final var expectedFixerResponse = new ExchangeratesIoApiResponse();
    expectedFixerResponse.setBase("USD");
    expectedFixerResponse.setRates(Map.of("EUR", 0.85, "GBP", 0.75));

    final var expectedResponse = new ExternalApiResponse();
    expectedResponse.setBase("USD");
    expectedResponse.setRates(Map.of("EUR", 0.85, "GBP", 0.75));

    var mockResponseEntity = new ResponseEntity<>(expectedFixerResponse, HttpStatus.OK);

    when(apiResponseMapper.exchangeratesIoToExternalApiResponse(expectedFixerResponse))
        .thenReturn(expectedResponse);
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class)))
        .thenReturn(mockResponseEntity);

    final var exchangeRates = exchangeratesIoProvider.getExchangeRates();

    assertEquals(List.of(expectedResponse), exchangeRates);

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class));
  }

  @Test
  void getExchangeRates_emptyBody() {

    ResponseEntity<ExchangeratesIoApiResponse> mockResponseEntity =
        new ResponseEntity<>(null, HttpStatus.OK);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class)))
        .thenReturn(mockResponseEntity);

    final var exchangeRates = exchangeratesIoProvider.getExchangeRates();

    assertTrue(exchangeRates.isEmpty());

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class));
  }

  @Test
  void getExchangeRates_unsuccessfulResponse() {

    ResponseEntity<ExchangeratesIoApiResponse> mockResponseEntity =
        new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class)))
        .thenReturn(mockResponseEntity);

    final var exchangeRates = exchangeratesIoProvider.getExchangeRates();

    assertTrue(exchangeRates.isEmpty());

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeratesIoApiResponse.class));
  }
}
