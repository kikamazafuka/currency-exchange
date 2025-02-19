package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.exception.CurrencyNotValidException;
import com.godeltech.currencyexchange.exception.NotFoundException;
import com.godeltech.currencyexchange.validator.CurrencyValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class ExchangeRateCacheService {

  private final Map<String, Map<String, Double>> exchangeRates;
  private final CurrencyValidator currencyValidator;

  @Autowired
  public ExchangeRateCacheService(
      Map<String, Map<String, Double>> exchangeRatesBean, CurrencyValidator currencyValidator) {
    this.exchangeRates = exchangeRatesBean;
    this.currencyValidator = currencyValidator;
  }

  public Map<String, Double> getCurrencyCacheExchangeRates(String currencyCode, Double amount) {

    if (!currencyValidator.isCurrencyValid(currencyCode)) {
      throw new CurrencyNotValidException("Currency with such currency code doesn't exists");
    }

    final var exchangeRates = this.exchangeRates.get(currencyCode);

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
}
