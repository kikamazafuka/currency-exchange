package com.godeltech.currencyexchange.provider;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class LocalIoProvider implements ExchangeRateProvider {

  @Value("${api.name.local}")
  private String providerName;

  @Value("${api.key.local}")
  private String apiKey;

  @Value("${api.url.local}")
  private String apiUrl;

  private final RestTemplate restTemplate;

  @Autowired
  public LocalIoProvider(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<ExternalApiResponse> getExchangeRates() {

    final var requestUrl = buildRequestUrl();

    final var responseEntity =
        restTemplate.exchange(
            requestUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ExternalApiResponse>>() {});

    if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
      return responseEntity.getBody();
    } else {
      log.error(
          "Failed to fetch {} exchange rates: {}",
          getProviderName(),
          responseEntity.getStatusCode());
    }
    return Collections.emptyList();
  }

  private String buildRequestUrl() {
    return UriComponentsBuilder.fromUriString(apiUrl + "/api/v1/local-rates")
        .queryParam("access_key", apiKey)
        .toUriString();
  }

  @Override
  public String getProviderName() {
    return providerName;
  }
}
