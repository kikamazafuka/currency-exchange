package com.godeltech.currencyexchange.provider;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class LocalIoProvider implements ExchangeRateProvider {

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
    final var requestUrl = apiUrl + "?" + "access_key" + "=" + apiKey;
    final var responseEntity =
        restTemplate.exchange(
            requestUrl,
            HttpMethod.GET,
            new HttpEntity<>(""),
            new ParameterizedTypeReference<List<ExternalApiResponse>>() {});
    final var apiResponse = responseEntity.getBody();
    //    List<ExternalApiResponse> response = null;
    //    try{
    //       response = restTemplate.getForObject(requestUrl, List.class);
    //    }catch (RestClientException e){
    //      log.error(e.getMessage());
    //    }

    if (apiResponse != null) {
      return apiResponse;
    }
    throw new RuntimeException("Failed to fetch exchange rates from Local.io");
  }

  @Override
  public String getProviderName() {
    return "Local.io";
  }
}
