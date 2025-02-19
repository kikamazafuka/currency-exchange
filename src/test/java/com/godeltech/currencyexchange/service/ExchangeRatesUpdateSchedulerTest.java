package com.godeltech.currencyexchange.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ExchangeRatesUpdateSchedulerTest {

  @MockitoBean private ExternalApiService externalApiService;

  @MockitoBean private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

  @Test
  void onApplicationReady() {

    verify(externalApiService).updateExchangeRates();
  }
}
