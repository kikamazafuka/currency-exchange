package com.godeltech.currencyexchange.validator;

import org.springframework.stereotype.Component;

@Component
public class CurrencyValidator {

  public boolean isCurrencyValid(String currencyCode) {
    return java.util.Currency.getAvailableCurrencies().stream()
        .anyMatch(currency -> currency.getCurrencyCode().equals(currencyCode));
  }
}
