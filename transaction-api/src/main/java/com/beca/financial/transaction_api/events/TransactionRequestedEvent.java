package com.beca.financial.transaction_api.events;

import com.beca.financial.transaction_api.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequestedEvent(
        UUID transactionId,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description,
        LocalDateTime createdAt
) {
}
