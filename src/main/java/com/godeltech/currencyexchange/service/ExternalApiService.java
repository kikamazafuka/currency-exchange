package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.exception.InvalidResponseException;
import com.godeltech.currencyexchange.provider.ExchangeRateProvider;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
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

  private final Map<String, Map<String, Double>> exchangeRates;

  private final List<ExchangeRateProvider> providers;

  @Autowired
  public ExternalApiService(
      Map<String, Map<String, Double>> exchangeRates, List<ExchangeRateProvider> providers) {
    this.exchangeRates = exchangeRates;
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
    exchangeRates.putAll(createRatesForSupportedCurrencies(bestRates));
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
          } catch (RestClientException | InvalidResponseException e) {
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

    final var baseCurrency = response.getBase();
    final var cachedRates = bestRates.computeIfAbsent(baseCurrency, k -> new ConcurrentHashMap<>());

    response.getRates().forEach(updateRatesWithMax(cachedRates));
  }

  private static BiConsumer<String, Double> updateRatesWithMax(Map<String, Double> cachedRates) {
    return (currency, rate) -> cachedRates.merge(currency, rate, Math::max);
  }

  public Map<String, Map<String, Double>> createRatesForSupportedCurrencies(
      Map<String, Map<String, Double>> bestRates) {

    final var reversedExchangeRates = new HashMap<>(bestRates);

    if (!reversedExchangeRates.isEmpty()) {

      final var baseCurrencyEntry = bestRates.entrySet().iterator().next();
      final var baseCurrencyRates = baseCurrencyEntry.getValue();
      final var baseCurrencyCode = baseCurrencyEntry.getKey();

      baseCurrencyRates.forEach(
          (fromCurr, fromRate) -> {
            baseCurrencyRates.forEach(
                (toCurr, toRate) ->
                    reversedExchangeRates
                        .computeIfAbsent(fromCurr, k -> new HashMap<>())
                        .put(toCurr, calculateRate(baseCurrencyRates, fromCurr, toCurr)));
            reversedExchangeRates
                .computeIfAbsent(fromCurr, k -> new HashMap<>())
                .put(baseCurrencyCode, getRateToBase(fromRate));
          });
    }
    return reversedExchangeRates;
  }

  private static double getRateToBase(Double fromRate) {
    return new BigDecimal(String.valueOf(1 / fromRate))
        .setScale(6, RoundingMode.HALF_UP)
        .doubleValue();
  }

  private static Double calculateRate(
      Map<String, Double> rates, String fromCurrency, String toCurrency) {

    final var toCurr = BigDecimal.valueOf(rates.get(toCurrency));
    final var fromCurr = BigDecimal.valueOf(rates.get(fromCurrency));
    final var result = toCurr.divide(fromCurr, 6, RoundingMode.HALF_UP);

    return result.doubleValue();
  }
}
