package com.godeltech.currencyexchange.provider;

import com.godeltech.currencyexchange.mapper.ApiResponseMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.provider.response.ExchangeratesIoApiResponse;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import com.godeltech.currencyexchange.service.ApiRequestLogService;
import com.godeltech.currencyexchange.service.CurrencyService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
public class ExchangeratesIoProvider implements ExchangeRateProvider {

  @Value("${api.name.exchangerates}")
  private String providerName;

  @Value("${api.key.exchangerates}")
  private String apiKey;

  @Value("${api.url.exchangerates}")
  private String apiUrl;

  private final RestTemplate restTemplate;

  private final ApiRequestLogService apiRequestLogService;

  private final ApiResponseMapper apiResponseMapper;

  private final CurrencyService currencyService;

  @Autowired
  public ExchangeratesIoProvider(
      RestTemplate restTemplate,
      ApiRequestLogService apiRequestLogService,
      ApiResponseMapper apiResponseMapper,
      CurrencyService currencyService) {
    this.restTemplate = restTemplate;
    this.apiRequestLogService = apiRequestLogService;
    this.apiResponseMapper = apiResponseMapper;
    this.currencyService = currencyService;
  }

  @Override
  public List<ExternalApiResponse> getExchangeRates() {

    List<ExternalApiResponse> responseExchangeRates = new ArrayList<>();

    final var requestUrl = buildRequestUrl();

    final var responseEntity =
        restTemplate.exchange(requestUrl, HttpMethod.GET, null, ExchangeratesIoApiResponse.class);

    if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {

      responseEntity.getBody().setRates(filterSupportedRates(responseEntity.getBody().getRates()));

      final var externalApiResponse =
          apiResponseMapper.exchangeratesIoToExternalApiResponse(responseEntity.getBody());

      apiRequestLogService.updateExternalApiRequestLogs(externalApiResponse, requestUrl);

      responseExchangeRates.add(externalApiResponse);
    } else {
      log.error(
          "Failed to fetch {} exchange rates: {}",
          getProviderName(),
          responseEntity.getStatusCode());
    }

    return responseExchangeRates;
  }

  private Map<String, Double> filterSupportedRates(Map<String, Double> rates) {
    final var supportedCurrencies =
        currencyService.getAllCurrencies().stream().map(Currency::getCurrencyCode).toList();
    return rates.entrySet().stream()
        .filter(entry -> supportedCurrencies.contains(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private String buildRequestUrl() {
    return UriComponentsBuilder.fromUriString(apiUrl + "/v1/latest")
        .queryParam("access_key", apiKey)
        .toUriString();
  }

  @Override
  public String getProviderName() {
    return providerName;
  }
}
