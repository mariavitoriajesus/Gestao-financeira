package com.beca.financial.transaction_processor.events;

import com.beca.financial.transaction_processor.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequestedEvent(
        UUID transactionId,
        UUID userId,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description,
        LocalDateTime createdAt
) {
}
