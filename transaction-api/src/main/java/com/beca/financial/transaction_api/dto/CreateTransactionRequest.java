package com.beca.financial.transaction_api.dto;

import com.beca.financial.transaction_api.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull UUID userId,
        @NotNull TransactionType type,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Size(min = 3, max = 3) String currency,
        @Size(min = 3, max = 3) String currencyFrom,
        @Size(min = 3, max = 3) String currencyTo,
        LocalDate quoteDate,
        @Size(max = 255) String description
        ) {
}
