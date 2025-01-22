package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.exception.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class ExchangeRateCacheService {

  private final Map<String, Map<String, Double>> exchangeRatesBean;

  @Autowired
  public ExchangeRateCacheService(Map<String, Map<String, Double>> exchangeRatesBean) {
    this.exchangeRatesBean = exchangeRatesBean;
  }

  public Map<String, Double> getExchangeRates(String baseCurrency) {
    return exchangeRatesBean.get(baseCurrency);
  }

  public Map<String, Double> getCurrencyCacheExchangeRates(String currencyCode, Double amount) {

    final var exchangeRates = exchangeRatesBean.get(currencyCode);
    if (exchangeRates == null) {
      throw new NotFoundException("Exchange rate for " + currencyCode + " not found.");
    }

    return amount == 0 ? exchangeRates : calculateAmount(exchangeRates, amount);
  }

  private static Map<String, Double> calculateAmount(
      Map<String, Double> exchangeRates, Double amount) {

    Map<String, Double> calculatedAmounts = new HashMap<>(exchangeRates);

    for (final var entry : calculatedAmounts.entrySet()) {
      entry.setValue(entry.getValue() * amount);
    }
    return calculatedAmounts;
  }
}
