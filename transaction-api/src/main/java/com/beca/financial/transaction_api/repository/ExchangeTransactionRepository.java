package com.beca.financial.transaction_api.repository;

import com.beca.financial.transaction_api.domain.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExchangeTransactionRepository extends JpaRepository<ExchangeTransaction, UUID> {
    Optional<ExchangeTransaction> findByTransactionId(UUID transactionId);
    boolean existsByTransactionId(UUID transactionId);
}
