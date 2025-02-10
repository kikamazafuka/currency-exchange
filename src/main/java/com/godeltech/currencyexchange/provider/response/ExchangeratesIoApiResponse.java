package com.godeltech.currencyexchange.provider.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeratesIoApiResponse {
  private String base;
  private String date;
  private Map<String, Double> rates;
}
