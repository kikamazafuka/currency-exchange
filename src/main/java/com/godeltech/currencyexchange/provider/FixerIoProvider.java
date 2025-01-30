package com.godeltech.currencyexchange.provider;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class FixerIoProvider implements ExchangeRateProvider {

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
    final var requestUrl = apiUrl + "?" + "access_key" + "=" + apiKey;

    ExternalApiResponse response = restTemplate.getForObject(requestUrl, ExternalApiResponse.class);

    if (response != null) {
      return List.of(response);
    }
    throw new RuntimeException("Failed to fetch exchange rates from Fixer.io");
  }

  @Override
  public String getProviderName() {
    return "Fixer.io";
  }
}
