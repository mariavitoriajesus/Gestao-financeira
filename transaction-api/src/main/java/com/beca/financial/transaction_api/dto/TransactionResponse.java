package com.beca.financial.transaction_api.dto;

import com.beca.financial.transaction_api.domain.enums.TransactionStatus;
import com.beca.financial.transaction_api.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description,
        TransactionStatus status,
        LocalDateTime createdAt
) {
}
