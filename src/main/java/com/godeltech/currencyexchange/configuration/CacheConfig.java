package com.godeltech.currencyexchange.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean
  public Map<String, Map<String, Double>> exchangeRates() {
    Map<String, Double> eurRates = new ConcurrentHashMap<>();
    eurRates.put("USD", 1.0417);
    eurRates.put("CAD", 1.2600);
    eurRates.put("GBP", 0.7191);

    Map<String, Map<String, Double>> exchangeRates = new ConcurrentHashMap<>();
    exchangeRates.put("EUR", eurRates);

    return exchangeRates;
  }
}
