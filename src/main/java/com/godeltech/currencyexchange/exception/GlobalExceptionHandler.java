package com.godeltech.currencyexchange.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
  public ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex) {

    final var errorMessages =
        ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();

    log.error("Validation error: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMessages);
  }
}
