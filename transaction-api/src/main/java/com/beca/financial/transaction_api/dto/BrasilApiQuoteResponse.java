package com.beca.financial.transaction_api.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiQuoteResponse(
        @JsonProperty("moeda") String moeda,
        @JsonProperty("data") LocalDate data,
        @JsonProperty("cotacoes") List<Cotacao> cotacoes
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cotacao(
            @JsonProperty("tipo_boletim") String tipoBoletim,
            @JsonProperty("cotacao_compra") BigDecimal cotacaoCompra,
            @JsonProperty("cotacao_venda") BigDecimal cotacaoVenda
    ) {}
}
