package com.godeltech.currencyexchange.controller;

import com.godeltech.currencyexchange.dto.CurrencyDto;
import com.godeltech.currencyexchange.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  @Autowired
  public CurrencyController(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  @Operation(
      summary = "Get all currencies stored in db",
      security = @SecurityRequirement(name = "basicAuth"),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of currencies",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CurrencyDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
      })
  @GetMapping
  public ResponseEntity<List<CurrencyDto>> getCurrencies() {

    log.info("CurrencyController::Getting list of all currencies");

    return ResponseEntity.ok(currencyService.getAllCurrencies());
  }

  @Operation(
      summary = "Add new currency to database",
      description = "This endpoint requires authentication and the ADMIN role",
      security = @SecurityRequirement(name = "basicAuth"),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Successfully added new currency",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CurrencyDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "409", description = "Currency with this code already exists"),
      })
  @PostMapping
  public ResponseEntity<CurrencyDto> addCurrency(
      @RequestParam
          @NotBlank(message = "{currency.code.notBlank}")
          @Pattern(regexp = "^[A-Z]{3}$", message = "{currency.code.pattern}")
          String currency) {

    log.info("CurrencyController::Adding new currency to database");

    final var currencyDto = currencyService.addCurrency(currency);

    return ResponseEntity.status(HttpStatus.CREATED).body(currencyDto);
  }
}
