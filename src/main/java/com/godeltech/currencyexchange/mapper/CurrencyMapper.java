package com.godeltech.currencyexchange.mapper;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrencyMapper {

  CurrencyDto currencyToCurrencyDto(Currency currency);

  Currency currencyDtoToCurrency(CurrencyDto currencyDto);

  List<CurrencyDto> currenciesToCurrencyDtos(List<Currency> currencies);
}
