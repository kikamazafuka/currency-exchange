package com.godeltech.currencyexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class CurrencyExchangeApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencyExchangeApplication.class, args);

  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
