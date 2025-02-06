package com.godeltech.currencyexchange.provider;

import com.godeltech.currencyexchange.provider.response.ExternalApiResponse;
import java.util.List;

public interface ExchangeRateProvider {
  List<ExternalApiResponse> getExchangeRates();

  String getProviderName();
}
