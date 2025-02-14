package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.exception.CurrencyNotValidException;
import com.godeltech.currencyexchange.exception.EntityAlreadyExistsException;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import com.godeltech.currencyexchange.validator.CurrencyValidator;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  private final CurrencyRepository currencyRepository;
  private final CurrencyMapper currencyMapper;
  private final CurrencyValidator currencyValidator;

  @Autowired
  public CurrencyService(
      CurrencyRepository currencyRepository,
      CurrencyMapper currencyMapper,
      CurrencyValidator currencyValidator) {
    this.currencyRepository = currencyRepository;
    this.currencyMapper = currencyMapper;
    this.currencyValidator = currencyValidator;
  }

  public List<CurrencyDto> getAllCurrencies() {

    List<Currency> currencies = currencyRepository.findAll();

    return currencyMapper.currenciesToCurrencyDtos(currencies);
  }

  @Transactional
  public CurrencyDto addCurrency(String currencyCode) {

    if (!currencyValidator.isCurrencyValid(currencyCode)) {
      throw new CurrencyNotValidException("Currency with such currency code doesn't exists");
    }
    if (existsByCurrencyCode(currencyCode)) {
      throw new EntityAlreadyExistsException("Currency with this code already exists");
    }

    final var currency = Currency.builder().currencyCode(currencyCode).build();

    currencyRepository.save(currency);

    return currencyMapper.currencyToCurrencyDto(currency);
  }

  public boolean existsByCurrencyCode(String currency) {
    return currencyRepository.existsByCurrencyCode(currency);
  }
}
