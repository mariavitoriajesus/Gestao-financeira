package com.beca.financial.transaction_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportSummaryResponse(
        LocalDate from,
        LocalDate to,
        long totalCount,
        BigDecimal totalAmount,
        BigDecimal totalCambio,
        BigDecimal totalEntrada,
        BigDecimal totalSaida,
        BigDecimal totalTransferencia
) {
}
