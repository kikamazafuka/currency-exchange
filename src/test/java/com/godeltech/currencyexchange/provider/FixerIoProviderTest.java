package com.godeltech.currencyexchange.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class FixerIoProviderTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private FixerIoProvider fixerIoProvider;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        fixerIoProvider, "apiUrl", "http://localhost:8080/api/v1/local-rates");
    ReflectionTestUtils.setField(fixerIoProvider, "apiKey", "fsdff");
  }

  @Test
  void getExchangeRates_successfulResponse() {

    ExternalApiResponse expectedResponse = new ExternalApiResponse();
    expectedResponse.setBase("USD");
    expectedResponse.setRates(Map.of("EUR", 0.85, "GBP", 0.75));

    var mockResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class)))
        .thenReturn(mockResponseEntity);

    List<ExternalApiResponse> exchangeRates = fixerIoProvider.getExchangeRates();

    assertEquals(List.of(expectedResponse), exchangeRates);

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class));
  }

  @Test
  void getExchangeRates_emptyBody() {

    ResponseEntity<ExternalApiResponse> mockResponseEntity =
        new ResponseEntity<>(null, HttpStatus.OK);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class)))
        .thenReturn(mockResponseEntity);

    List<ExternalApiResponse> exchangeRates = fixerIoProvider.getExchangeRates();

    assertTrue(exchangeRates.isEmpty());

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class));
  }

  @Test
  void getExchangeRates_unsuccessfulResponse() {

    ResponseEntity<ExternalApiResponse> mockResponseEntity =
        new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class)))
        .thenReturn(mockResponseEntity);

    List<ExternalApiResponse> exchangeRates = fixerIoProvider.getExchangeRates();

    assertTrue(exchangeRates.isEmpty());

    verify(restTemplate)
        .exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExternalApiResponse.class));
  }
}
