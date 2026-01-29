package com.beca.financial.transaction_api.builder;

import com.beca.financial.transaction_api.domain.ExchangeTransaction;
import com.beca.financial.transaction_api.dto.ExchangeRateResponse;

import java.util.UUID;

public final class ExchangeRateResponseBuilder {

    public ExchangeRateResponseBuilder() {
        // impede instância
    }

    public static ExchangeRateResponse fromEntity(ExchangeTransaction entity) {
        if (entity == null) {
            return null;
        }

        return new ExchangeRateResponse(
                entity.getCurrencyFrom(),
                entity.getCurrencyTo(),
                entity.getRate(),
                entity.getAmountFrom(),
                entity.getAmountTo(),
                entity.getQuoteDate(),
                entity.getSource()
        );
    }

    public Object setTransactionId(UUID transactionId) {
        return null;
    }
}
