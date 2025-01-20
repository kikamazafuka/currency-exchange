package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.exception.CurrencyAlreadyExistsException;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  private final CurrencyRepository currencyRepository;
  private final CurrencyMapper currencyMapper;

  @Autowired
  public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
    this.currencyRepository = currencyRepository;
    this.currencyMapper = currencyMapper;
  }

  public List<Currency> getAllCurrencies() {
    return currencyRepository.findAll();
  }

  @Transactional
  public CurrencyDto addCurrency(String currencyCode) {

    if (existsByCurrencyCode(currencyCode)) {
      throw new CurrencyAlreadyExistsException("Currency with this code already exists");
    }

    final var currency = Currency.builder().currencyCode(currencyCode).build();

    currencyRepository.save(currency);

    return currencyMapper.currencyToCurrencyDto(currency);
  }

  public boolean existsByCurrencyCode(String currency) {
    return currencyRepository.existsByCurrencyCode(currency);
  }
}
