package com.godeltech.currencyexchange.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.model.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CurrencyMapperTest {

  private final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

  @Test
  void currencyToCurrencyDto() {
    Currency currency = new Currency();
    currency.setCurrencyCode("USD");

    CurrencyDto currencyDto = currencyMapper.currencyToCurrencyDto(currency);

    assertEquals(currency.getCurrencyCode(), currencyDto.currencyCode());
  }

  @Test
  void currencyDtoToCurrency() {
    CurrencyDto currencyDto = new CurrencyDto("USD");

    Currency currency = currencyMapper.currencyDtoToCurrency(currencyDto);

    assertEquals(currency.getCurrencyCode(), currencyDto.currencyCode());
  }

  @Test
  void currenciesToCurrencyDtos() {
    List<Currency> currencies =
        List.of(
            Currency.builder().id(1L).currencyCode("USD").build(),
            Currency.builder().id(2L).currencyCode("EUR").build());
    List<CurrencyDto> currencyDTOs = currencyMapper.currenciesToCurrencyDtos(currencies);
    assertThat(currencyDTOs.get(0).currencyCode()).isEqualTo("USD");
    assertThat(currencyDTOs.get(1).currencyCode()).isEqualTo("EUR");
  }
}
