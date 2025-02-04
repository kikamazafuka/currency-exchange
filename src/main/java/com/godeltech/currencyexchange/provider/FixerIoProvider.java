package com.godeltech.currencyexchange.provider;

import com.godeltech.currencyexchange.service.ApiRequestLogService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class FixerIoProvider implements ExchangeRateProvider {

  @Value("${api.name.fixer}")
  private String providerName;

  @Value("${api.key.fixer}")
  private String apiKey;

  @Value("${api.url.fixer}")
  private String apiUrl;

  private final RestTemplate restTemplate;

  private final ApiRequestLogService apiRequestLogService;

  @Autowired
  public FixerIoProvider(RestTemplate restTemplate, ApiRequestLogService apiRequestLogService) {
    this.restTemplate = restTemplate;
    this.apiRequestLogService = apiRequestLogService;
  }

  @Override
  public List<ExternalApiResponse> getExchangeRates() {

    List<ExternalApiResponse> responseExchangeRates = new ArrayList<>();

    final var requestUrl = buildRequestUrl();

    final var responseEntity =
        restTemplate.exchange(requestUrl, HttpMethod.GET, null, ExternalApiResponse.class);

    if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {

      apiRequestLogService.updateExternalApiRequestLogs(responseEntity.getBody(), requestUrl);

      responseExchangeRates.add(responseEntity.getBody());
    } else {
      log.error(
          "Failed to fetch {} exchange rates: {}",
          getProviderName(),
          responseEntity.getStatusCode());
    }

    return responseExchangeRates;
  }

  private String buildRequestUrl() {
    return UriComponentsBuilder.fromUriString(apiUrl + "/api/latest")
        .queryParam("access_key", apiKey)
        .toUriString();
  }

  @Override
  public String getProviderName() {
    return providerName;
  }
}
