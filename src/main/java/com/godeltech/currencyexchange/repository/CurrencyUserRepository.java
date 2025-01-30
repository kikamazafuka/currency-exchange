package com.godeltech.currencyexchange.repository;

import com.godeltech.currencyexchange.model.CurrencyUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyUserRepository extends JpaRepository<CurrencyUser, Long> {
  Optional<CurrencyUser> findByUsername(String username);
}
