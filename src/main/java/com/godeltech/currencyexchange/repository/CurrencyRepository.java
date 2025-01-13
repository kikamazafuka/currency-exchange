package com.godeltech.currencyexchange.repository;

import com.godeltech.currencyexchange.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {}
