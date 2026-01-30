package com.beca.financial.user_service.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MockBalanceResponse(
        UUID userId,
        String currency,
        BigDecimal currentBalance,
        BigDecimal avaliableBalance,
        BigDecimal overdraftLimit,
        String status
) {
}
