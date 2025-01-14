package com.godeltech.currencyexchange.controller;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currencies")
@Slf4j
public class CurrencyController {

  private final CurrencyService currencyService;
  private final CurrencyMapper currencyMapper;

  @Autowired
  public CurrencyController(CurrencyService currencyService, CurrencyMapper currencyMapper) {
    this.currencyService = currencyService;
    this.currencyMapper = currencyMapper;
  }

  @Operation(summary = "Get all currencies stored in db")
  @GetMapping
  public ResponseEntity<List<CurrencyDto>> getCurrencies() {

    log.info("CurrencyController::Getting list of all currencies");
    List<Currency> allCurrencies = currencyService.getAllCurrencies();
    return ResponseEntity.ok(currencyMapper.currenciesToCurrencyDtos(allCurrencies));
  }
}
