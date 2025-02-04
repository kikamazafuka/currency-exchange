package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.provider.ExchangeRateProvider;
import com.godeltech.currencyexchange.provider.ExternalApiResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class ExternalApiService {

  private final Map<String, Map<String, Double>> exchangeRatesBean;

  private final List<ExchangeRateProvider> providers;

  @Autowired
  public ExternalApiService(
      Map<String, Map<String, Double>> exchangeRatesBean, List<ExchangeRateProvider> providers) {
    this.exchangeRatesBean = exchangeRatesBean;
    this.providers = providers;
  }

  @PostConstruct
  public void init() {
    updateExchangeRates();
  }

  @Scheduled(fixedDelayString = "${fixedRate.in.milliseconds}")
  public void updateExchangeRates() {
    updateCacheWithBestRates(getBestRatesFromProviders());
  }

  private void updateCacheWithBestRates(Map<String, Map<String, Double>> bestRates) {
    exchangeRatesBean.putAll(bestRates);
  }

  private Map<String, Map<String, Double>> getBestRatesFromProviders() {

    Map<String, Map<String, Double>> bestRates = new ConcurrentHashMap<>();

    providers.forEach(
        provider -> {
          log.info("Fetching exchange rates using {} API", provider.getProviderName());
          try {
            provider
                .getExchangeRates()
                .forEach(response -> createExchangeRatesFromResponse(response, bestRates));
          } catch (RestClientException e) {
            log.error(
                "Error getting exchange rates with {} API: {}",
                provider.getProviderName(),
                e.getMessage());
          }
        });

    return bestRates;
  }

  private void createExchangeRatesFromResponse(
      ExternalApiResponse response, Map<String, Map<String, Double>> bestRates) {

    String baseCurrency = response.getBase();

    Map<String, Double> cachedRates =
        bestRates.computeIfAbsent(baseCurrency, k -> new ConcurrentHashMap<>());

    response.getRates().forEach(updateRatesWithMax(cachedRates));
  }

  private static BiConsumer<String, Double> updateRatesWithMax(Map<String, Double> cachedRates) {
    return (currency, rate) -> cachedRates.merge(currency, rate, Math::max);
  }
}
