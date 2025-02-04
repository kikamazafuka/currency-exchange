package com.godeltech.currencyexchange.provider;

import java.util.Collections;
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

  @Autowired
  public FixerIoProvider(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<ExternalApiResponse> getExchangeRates() {

    final var requestUrl = buildRequestUrl();

    final var responseEntity =
        restTemplate.exchange(requestUrl, HttpMethod.GET, null, ExternalApiResponse.class);

    if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
      return List.of(responseEntity.getBody());
    } else {
      log.error(
          "Failed to fetch {} exchange rates: {}",
          getProviderName(),
          responseEntity.getStatusCode());
    }

    return Collections.emptyList();
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
