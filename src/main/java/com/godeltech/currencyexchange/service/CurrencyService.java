package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  private final CurrencyRepository currencyRepository;

  @Autowired
  public CurrencyService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
  }

  public List<Currency> getAllCurrencies() {
    return currencyRepository.findAll();
  }
}
