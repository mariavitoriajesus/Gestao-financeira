package com.beca.financial.transaction_api.repository;

import com.beca.financial.transaction_api.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
