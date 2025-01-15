package com.godeltech.currencyexchange.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.model.Currency;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CurrencyMapperTest {

  private CurrencyMapper currencyMapper;

  @BeforeEach
  void setUp() {
    currencyMapper = Mappers.getMapper(CurrencyMapper.class);
  }

  @Test
  void currencyToCurrencyDto() {
    final var currency = new Currency();
    currency.setCurrencyCode("USD");

    final var currencyDto = currencyMapper.currencyToCurrencyDto(currency);

    assertEquals(currency.getCurrencyCode(), currencyDto.currencyCode());
  }

  @Test
  void currencyDtoToCurrency() {
    final var currencyDto = new CurrencyDto("USD");

    final var currency = currencyMapper.currencyDtoToCurrency(currencyDto);

    assertEquals(currency.getCurrencyCode(), currencyDto.currencyCode());
  }

  @Test
  void currenciesToCurrencyDtos() {
    final var currencies =
        List.of(
            Currency.builder().id(1L).currencyCode("USD").build(),
            Currency.builder().id(2L).currencyCode("EUR").build());

    final var expectedCurrencyDtos = List.of(new CurrencyDto("USD"), new CurrencyDto("EUR"));

    final var currencyDTOs = currencyMapper.currenciesToCurrencyDtos(currencies);

    assertThat(currencyDTOs).isEqualTo(expectedCurrencyDtos);
  }
}
