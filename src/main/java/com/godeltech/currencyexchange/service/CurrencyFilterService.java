package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyFilterService {

  private final CurrencyRepository currencyRepository;

  @Autowired
  public CurrencyFilterService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
  }

  public Map<String, Double> filterSupportedRates(Map<String, Double> rates) {
    List<String> supportedCurrencies =
        currencyRepository.findAll().stream().map(Currency::getCurrencyCode).toList();
    return rates.entrySet().stream()
        .filter(entry -> supportedCurrencies.contains(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
