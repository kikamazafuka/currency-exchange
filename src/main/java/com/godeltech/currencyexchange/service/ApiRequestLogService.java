package com.godeltech.currencyexchange.service;

import com.godeltech.currencyexchange.exception.InvalidResponseException;
import com.godeltech.currencyexchange.model.ApiRequestLog;
import com.godeltech.currencyexchange.provider.ExternalApiResponse;
import com.godeltech.currencyexchange.repository.ApiRequestLogRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiRequestLogService {

  private final ApiRequestLogRepository apiRequestLogRepository;

  @Autowired
  public ApiRequestLogService(ApiRequestLogRepository apiRequestLogRepository) {
    this.apiRequestLogRepository = apiRequestLogRepository;
  }

  @Transactional
  public void updateExternalApiRequestLogs(ExternalApiResponse response, String url) {

    if (response == null || response.getRates() == null) {
      log.error("Invalid response: No rates found.");
      throw new InvalidResponseException("Invalid response: No rates found.");
    }

    String baseCurrency = response.getBase();

    Map<String, Double> rates = response.getRates();

    rates.forEach(
        (currency, rate) -> {
          ApiRequestLog log =
              ApiRequestLog.builder()
                  .timestamp(LocalDateTime.now())
                  .url(url)
                  .requestCurrency(baseCurrency)
                  .targetCurrency(currency)
                  .currencyRate(rate)
                  .build();

          apiRequestLogRepository.save(log);
        });
  }
}
