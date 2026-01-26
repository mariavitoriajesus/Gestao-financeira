package com.beca.financial.transaction_api.dto;

import com.beca.financial.transaction_api.domain.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionStatusRequest(
        @NotNull TransactionStatus status
        ) {
}
