package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock private CurrencyRepository currencyRepository;

  @InjectMocks private CurrencyService currencyService;

  private Currency eur;
  private Currency usd;

  @BeforeEach
  public void setup() {
    eur = Currency.builder().id(1L).currencyCode("EUR").build();
    usd = Currency.builder().id(2L).currencyCode("USD").build();
  }

  @Test
  void getAllCurrencies() {

    when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

    List<Currency> currencies = currencyService.getAllCurrencies();

    assertThat(currencies).isNotNull();
    assertThat(currencies.size()).isGreaterThan(1);
  }
}
