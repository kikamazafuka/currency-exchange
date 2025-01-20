package com.godeltech.currencyexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.exception.CurrencyAlreadyExistsException;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
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
  @Mock private CurrencyMapper currencyMapper;

  @InjectMocks private CurrencyService currencyService;

  private Currency eur;
  private Currency usd;
  private CurrencyDto usdDto;
  private String currencyCode;

  @BeforeEach
  public void setup() {
    eur = Currency.builder().id(1L).currencyCode("EUR").build();
    usd = Currency.builder().id(2L).currencyCode("USD").build();
    usdDto = new CurrencyDto("USD");
    currencyCode = "USD";
  }

  @Test
  void getAllCurrencies() {

    when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

    final var currencies = currencyService.getAllCurrencies();

    assertThat(currencies).isNotNull().containsExactly(usd, eur);
  }

  @Test
  public void addCurrency() {

    final var currencyCode = "USD";

    final var mockCurrency = new Currency(null, currencyCode);

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(false);
    when(currencyRepository.save(mockCurrency)).thenReturn(mockCurrency);
    when(currencyMapper.currencyToCurrencyDto(mockCurrency)).thenReturn(usdDto);

    final var actualDto = currencyService.addCurrency(currencyCode);

    assertEquals(usdDto, actualDto);
  }

  @Test
  public void testAddCurrency_throwsCurrencyAlreadyExistsException() {

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(true);

    final var exception =
        assertThrows(
            CurrencyAlreadyExistsException.class,
            () -> currencyService.addCurrency(currencyCode),
            "Expected addCurrency() to throw CurrencyAlreadyExistsException");

    assertEquals("Currency with this code already exists", exception.getMessage());
  }

  @Test
  void existsByCurrencyCode_returnTrue() {

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(true);

    final var result = currencyService.existsByCurrencyCode(currencyCode);

    assertTrue(result);
  }

  @Test
  void existsByCurrencyCode_returnFalse() {

    when(currencyRepository.existsByCurrencyCode(currencyCode)).thenReturn(false);

    final var result = currencyService.existsByCurrencyCode(currencyCode);

    assertFalse(result);
  }
}
