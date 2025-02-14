package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyFilterService {

  private final CurrencyService currencyService;

  @Autowired
  public CurrencyFilterService(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  public Map<String, Double> filterSupportedRates(Map<String, Double> rates) {
    List<String> supportedCurrencies =
        currencyService.getAllCurrencies().stream().map(CurrencyDto::currencyCode).toList();
    return rates.entrySet().stream()
        .filter(entry -> supportedCurrencies.contains(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
