package com.godeltech.currencyexchange.validator;

import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValidator {

  public boolean isCurrencyValid(String currencyCode) {
    return Currency.getAvailableCurrencies().stream()
        .anyMatch(currency -> currency.getCurrencyCode().equals(currencyCode));
  }
}
