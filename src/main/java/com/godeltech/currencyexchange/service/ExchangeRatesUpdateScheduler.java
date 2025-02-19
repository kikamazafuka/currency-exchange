package com.godeltech.currencyexchange.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesUpdateScheduler {

  private final ExternalApiService externalApiService;

  public ExchangeRatesUpdateScheduler(ExternalApiService externalApiService) {
    this.externalApiService = externalApiService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    externalApiService.updateExchangeRates();
  }

  @Scheduled(fixedDelayString = "${fixedRate.in.milliseconds}")
  public void updateRatesBySchedule() {
    externalApiService.updateExchangeRates();
  }
}
