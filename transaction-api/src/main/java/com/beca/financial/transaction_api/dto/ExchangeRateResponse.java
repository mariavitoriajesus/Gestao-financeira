package com.beca.financial.transaction_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(
        String currencyFrom,
        String currencyTo,
        BigDecimal rate,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        LocalDate quoteDate,
        String source
) {

}
