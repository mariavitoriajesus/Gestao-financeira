package com.beca.financial.transaction_api.dto;


import java.math.BigDecimal;
import java.util.List;

public record BrasilApiQuoteResponse(
        String moeda,
        String data,
        List<Cotacao> cotacao
) {
    public record Cotacao(
            String tipoBoletim,
            BigDecimal cotacaoCompra,
            BigDecimal cotacaoVenda
    ) {}
}
