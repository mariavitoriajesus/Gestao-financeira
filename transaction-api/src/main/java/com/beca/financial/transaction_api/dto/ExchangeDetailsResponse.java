package com.beca.financial.transaction_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExchangeDetailsResponse(
        UUID transactionId,
        String currencyFrom,
        String currencyTo,
        BigDecimal rate,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        LocalDate quoteDate,
        String source
) {
}
