package com.godeltech.currencyexchange.controller;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.mapper.CurrencyMapper;
import com.godeltech.currencyexchange.model.Currency;
import com.godeltech.currencyexchange.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currencies")
@Slf4j
@Validated
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
    final var allCurrencies = currencyService.getAllCurrencies();
    return ResponseEntity.ok(currencyMapper.currenciesToCurrencyDtos(allCurrencies));
  }

  @Operation(summary = "Add new currency to database")
  @PostMapping
  public ResponseEntity<CurrencyDto> addCurrency(
      @RequestParam
          @NotBlank(message = "{currency.code.notBlank}")
          @Pattern(regexp = "^[A-Z]{3}$", message = "{currency.code.pattern}")
          String currency) {

    log.info("CurrencyController::Adding new currency to database");

    final var currencyDto = currencyService.addCurrency(currency);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(currencyDto);
  }
}
