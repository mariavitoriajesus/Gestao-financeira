package com.beca.financial.transaction_api.dto;

import java.util.UUID;

public record CreateTransactionResponse(
        UUID transactionId
) {
}
