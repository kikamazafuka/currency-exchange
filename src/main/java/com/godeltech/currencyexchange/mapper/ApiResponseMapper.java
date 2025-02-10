package com.godeltech.currencyexchange.mapper;

import com.godeltech.currencyexchange.provider.response.ExchangeratesIoApiResponse;
import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import com.godeltech.currencyexchange.provider.response.FixerIoApiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiResponseMapper {

  ExternalApiResponse fixerToExternalApiResponse(FixerIoApiResponse fixerIoResponse);

  ExternalApiResponse exchangeratesIoToExternalApiResponse(
      ExchangeratesIoApiResponse exchangeratesIoApiResponse);
}
