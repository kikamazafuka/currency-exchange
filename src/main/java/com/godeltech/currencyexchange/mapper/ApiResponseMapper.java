package com.godeltech.currencyexchange.mapper;

import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import com.godeltech.currencyexchange.provider.response.FixerIoApiResponse;
import com.godeltech.currencyexchange.provider.response.LocalApiResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiResponseMapper {

  ExternalApiResponse toExternalApiResponse(FixerIoApiResponse fixerIoResponse);

  ExternalApiResponse toExternalApiResponse(LocalApiResponse localIoResponse);

  List<ExternalApiResponse> toExternalApiResponseList(List<LocalApiResponse> localIoResponse);
}
