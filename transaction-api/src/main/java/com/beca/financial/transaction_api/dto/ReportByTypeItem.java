package com.beca.financial.transaction_api.dto;

import com.beca.financial.transaction_api.domain.enums.TransactionType;

import java.math.BigDecimal;

public record ReportByTypeItem(
        TransactionType type,
        long count,
        BigDecimal totalAmount
) {
}
