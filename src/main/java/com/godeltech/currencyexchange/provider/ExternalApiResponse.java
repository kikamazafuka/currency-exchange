package com.godeltech.currencyexchange.provider;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalApiResponse {
  private String base;
  private String date;
  private Map<String, Double> rates;
}
