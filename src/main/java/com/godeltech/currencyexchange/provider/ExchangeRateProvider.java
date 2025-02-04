package com.godeltech.currencyexchange.provider;

import java.util.List;

public interface ExchangeRateProvider {
  List<ExternalApiResponse> getExchangeRates();

  String getProviderName();
}
