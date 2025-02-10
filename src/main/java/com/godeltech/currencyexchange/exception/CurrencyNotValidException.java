package com.godeltech.currencyexchange.exception;

public class CurrencyNotValidException extends RuntimeException {
  public CurrencyNotValidException(String message) {
    super(message);
  }
}
