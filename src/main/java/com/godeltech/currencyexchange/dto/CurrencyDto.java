package com.godeltech.currencyexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record CurrencyDto(
    @NotBlank(message = "Currency code cannot be empty") String currencyCode) {}
