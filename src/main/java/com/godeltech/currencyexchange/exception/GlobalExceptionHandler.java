package com.godeltech.currencyexchange.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<List<String>> handleValidationExceptions(ConstraintViolationException ex) {

    final var errorMessages =
        ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();

    log.error("Validation error: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorMessages);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<String> handleCurrencyAlreadyExists(EntityAlreadyExistsException ex) {

    log.error("Entity already exists");

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"message\": \"" + ex.getMessage() + "\"}");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleCurrencyCacheExchangeRates(NotFoundException ex) {

    log.error("Requested data was not found");

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"message\": \"" + ex.getMessage() + "\"}");
  }
}
