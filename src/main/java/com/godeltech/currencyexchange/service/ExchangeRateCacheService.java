package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.exception.NotFoundException;
import com.godeltech.currencyexchange.provider.ExchangeRateProvider;
import com.godeltech.currencyexchange.provider.ExternalApiResponse;
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
import org.springframework.web.client.ResourceAccessException;

@Service
@Slf4j
public final class ExchangeRateCacheService {

  private final Map<String, Map<String, Double>> exchangeRatesBean;

  private final List<ExchangeRateProvider> providers;

  @Autowired
  public ExchangeRateCacheService(
      Map<String, Map<String, Double>> exchangeRatesBean, List<ExchangeRateProvider> providers) {
    this.exchangeRatesBean = exchangeRatesBean;
    this.providers = providers;
  }

  public Map<String, Double> getExchangeRates(String baseCurrency) {
    return exchangeRatesBean.get(baseCurrency);
  }

  @PostConstruct
  public void init() {
    updateExchangeRates();
  }

  public Map<String, Double> getCurrencyCacheExchangeRates(String currencyCode, Double amount) {

    final var exchangeRates = exchangeRatesBean.get(currencyCode);
    if (exchangeRates == null) {
      throw new NotFoundException("Exchange rate for " + currencyCode + " not found.");
    }

    return calculateAmount(exchangeRates, amount);
  }

  private static Map<String, Double> calculateAmount(
      Map<String, Double> exchangeRates, Double amount) {

    Map<String, Double> calculatedAmounts = new HashMap<>(exchangeRates);

    for (final var entry : calculatedAmounts.entrySet()) {
      final var calculatedValue = entry.getValue() * amount;
      final var roundedValue =
          new BigDecimal(String.valueOf(calculatedValue)).setScale(6, RoundingMode.HALF_UP);
      entry.setValue(roundedValue.doubleValue());
    }
    return calculatedAmounts;
  }

  @Scheduled(fixedDelayString = "${fixedRate.in.milliseconds}")
  public void updateExchangeRates() {
    providers.forEach(provider -> {
      log.info("Updating exchange rates using {} API", provider.getProviderName());
      try {
        provider.getExchangeRates().forEach(this::updateExchangeRatesFromResponse);
      } catch (ResourceAccessException e) {
        log.error("Error updating exchange rates with {} API: {}", provider.getProviderName(), e.getMessage());
      }
    });
  }

  private void updateExchangeRatesFromResponse(ExternalApiResponse response) {
    String baseCurrency = response.getBase();
    Map<String, Double> cachedRates = exchangeRatesBean.computeIfAbsent(baseCurrency, k -> new ConcurrentHashMap<>());
    response.getRates().forEach(updateCacheRatesWithMax(cachedRates));
  }

  private static BiConsumer<String, Double> updateCacheRatesWithMax(
      Map<String, Double> cachedRates) {
    return (currency, rate) -> cachedRates.put(currency, getMax(cachedRates, currency, rate));
  }

  private static double getMax(Map<String, Double> cachedRates, String currency, Double rate) {
    return Math.max(cachedRates.getOrDefault(currency, 0.01), rate);
  }
}
