package com.godeltech.currencyexchange.provider;

import java.util.List;
import java.util.Map;

public interface ExchangeRateProvider {
    List<ExternalApiResponse> getExchangeRates();
    String getProviderName();
}
