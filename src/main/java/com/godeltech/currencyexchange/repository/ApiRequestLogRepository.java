package com.godeltech.currencyexchange.repository;

import com.godeltech.currencyexchange.model.ApiRequestLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, UUID> {}
