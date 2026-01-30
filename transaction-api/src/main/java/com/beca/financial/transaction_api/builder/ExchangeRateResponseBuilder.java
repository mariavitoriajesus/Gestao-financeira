package com.beca.financial.transaction_api.builder;

import com.beca.financial.transaction_api.dto.ExchangeRateResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRateResponseBuilder {

    private String currencyFrom;
    private String currencyTo;
    private BigDecimal rate;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private LocalDate quoteDate;
    private String source;

    public ExchangeRateResponseBuilder currencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
        return this;
    }

    public ExchangeRateResponseBuilder currencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
        return this;
    }

    public ExchangeRateResponseBuilder rate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public ExchangeRateResponseBuilder amountFrom(BigDecimal amountFrom) {
        this.amountFrom = amountFrom;
        return this;
    }

    public ExchangeRateResponseBuilder amountTo(BigDecimal amountTo) {
        this.amountTo = amountTo;
        return this;
    }

    public ExchangeRateResponseBuilder quoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
        return this;
    }

    public ExchangeRateResponseBuilder source(String source) {
        this.source = source;
        return this;
    }

    public ExchangeRateResponse build() {
        return new ExchangeRateResponse(
                currencyFrom,
                currencyTo,
                rate,
                amountFrom,
                amountTo,
                quoteDate,
                source
        );
    }
}
