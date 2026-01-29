package com.beca.financial.transaction_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(
        String moeda,
        String data,
        BigDecimal cotacaoCompra,
        BigDecimal cotacaoVenda,
        BigDecimal amountTo,
        LocalDate quoteDate,
        String source
) {

    public record Cotacao(
            @JsonProperty("cotacao_compra") BigDecimal cotacaoCompra,
            @JsonProperty("cotacao_venda") BigDecimal cotacaoVenda,
            @JsonProperty("tipo_boletim") String tipoBoletim,
            @JsonProperty("data_hora_cotacao") String dataHoraCotacao
    ) {}
}
