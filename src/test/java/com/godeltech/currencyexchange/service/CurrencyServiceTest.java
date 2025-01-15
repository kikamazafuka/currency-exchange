package com.godeltech.currencyexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.repository.CurrencyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    final var currencies = currencyService.getAllCurrencies();

    assertThat(currencies).isNotNull();
    assertThat(currencies).containsExactly(usd, eur);
  }
}
