package com.godeltech.currencyexchange.controller;

import com.godeltech.currencyexchange.service.ExchangeRateCacheService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@Validated
public class ExchangeRatesController {

  private final ExchangeRateCacheService exchangeRateCacheService;

  @Autowired
  public ExchangeRatesController(ExchangeRateCacheService exchangeRateCacheService) {
    this.exchangeRateCacheService = exchangeRateCacheService;
  }

  @Operation(summary = "Get latest exchange rates from cache")
  @GetMapping("/exchange-rates")
  public ResponseEntity<Map<String, Double>> getExchangeRate(
      @RequestParam
          @NotBlank(message = "{currency.code.notBlank}")
          @Pattern(regexp = "^[A-Z]{3}$", message = "{currency.code.pattern}")
          String currency,
      @RequestParam
          @NotNull(message = "{exchangerate.amount.notnull}")
          @DecimalMin(value = "1.0", message = "{exchangerate.amount.decimal}")
          Double amount) {

    log.info("CurrencyController::Getting latest exchange rates from cache");

    final var rate = exchangeRateCacheService.getCurrencyCacheExchangeRates(currency, amount);

    return ResponseEntity.status(HttpStatus.OK).body(rate);
  }
}
