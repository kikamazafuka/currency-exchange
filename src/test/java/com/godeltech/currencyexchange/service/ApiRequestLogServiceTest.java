package com.godeltech.currencyexchange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.exception.InvalidResponseException;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import com.godeltech.currencyexchange.repository.ApiRequestLogRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiRequestLogServiceTest {

  @Mock private ApiRequestLogRepository apiRequestLogRepository;

  @Mock private ExternalApiResponse mockResponse;

  @InjectMocks private ApiRequestLogService apiRequestLogService;

  private String url;

  @BeforeEach
  public void setup() {
    url = "https://api.provider.com/rates";
  }

  @Test
  void updateExternalApiRequestLogs() {

    String baseCurrency = "USD";
    Map<String, Double> rates = Map.of("EUR", 0.85, "GBP", 0.75);

    when(mockResponse.getBase()).thenReturn(baseCurrency);
    when(mockResponse.getRates()).thenReturn(rates);

    apiRequestLogService.updateExternalApiRequestLogs(mockResponse, url);

    rates.forEach(
        (currency, rate) -> {
          verify(apiRequestLogRepository)
              .save(
                  argThat(
                      apiRequestLog ->
                          apiRequestLog.getUrl().equals(url)
                              && apiRequestLog.getRequestCurrency().equals(baseCurrency)
                              && apiRequestLog.getTargetCurrency().equals(currency)
                              && apiRequestLog.getCurrencyRate().equals(rate)));
        });
  }

  @Test
  public void updateExternalApiRequestLogs_invalidResponse_throwsException() {

    when(mockResponse.getRates()).thenReturn(null);

    InvalidResponseException exception =
        assertThrows(
            InvalidResponseException.class,
            () -> apiRequestLogService.updateExternalApiRequestLogs(mockResponse, url));
    assertEquals("Invalid response: No rates found.", exception.getMessage());
  }

  @Test
  public void updateExternalApiRequestLogs_nullResponse_throwsException() {

    mockResponse = null;

    InvalidResponseException exception =
        assertThrows(
            InvalidResponseException.class,
            () -> apiRequestLogService.updateExternalApiRequestLogs(mockResponse, url));
    assertEquals("Invalid response: No rates found.", exception.getMessage());
  }
}
