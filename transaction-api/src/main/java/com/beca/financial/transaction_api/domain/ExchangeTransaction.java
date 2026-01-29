package com.beca.financial.transaction_api.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "exchange_transactions")
public class ExchangeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "currency_from", nullable = false, length = 3)
    private String currencyFrom;

    @Column(name = "currency_to", nullable = false, length = 3)
    private String currencyTo;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal rate;

    @Column(name = "amount_from", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountFrom;

    @Column(name = "amount_to", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountTo;

    @Column(name = "quote_date", nullable = false)
    private LocalDate quoteDate;

    @Column(nullable = false, length = 30)
    private String source;


    public void setId(UUID id) {
        this.id = id;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setAmountFrom(BigDecimal amountFrom) {
        this.amountFrom = amountFrom;
    }

    public void setAmountTo(BigDecimal amountTo) {
        this.amountTo = amountTo;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmountFrom() {
        return amountFrom;
    }

    public BigDecimal getAmountTo() {
        return amountTo;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }

    public String getSource() {
        return source;
    }
}
