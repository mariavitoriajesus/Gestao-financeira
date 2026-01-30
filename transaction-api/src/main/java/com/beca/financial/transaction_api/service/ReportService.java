package com.beca.financial.transaction_api.service;


import com.beca.financial.transaction_api.domain.enums.TransactionType;
import com.beca.financial.transaction_api.dto.ReportByTypeItem;
import com.beca.financial.transaction_api.dto.ReportSummaryResponse;
import com.beca.financial.transaction_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportByTypeItem> byType(LocalDate from, LocalDate toInclusive) {
        DateRange range = range(from, toInclusive);
        return transactionRepository.reportByType(range.from,range.to);
    }

    public ReportSummaryResponse summary(LocalDate from, LocalDate toInclusive) {
        DateRange range = range(from, toInclusive);

        long count = transactionRepository.totalCount(range.from, range.to);
        BigDecimal total = transactionRepository.totalAmount(range.from, range.to);

        BigDecimal cambio = transactionRepository.totalByType(TransactionType.CAMBIO, range.from, range.to);
        BigDecimal entrada = transactionRepository.totalByType(TransactionType.ENTRADA, range.from, range.to);
        BigDecimal saida = transactionRepository.totalByType(TransactionType.SAIDA, range.from, range.to);
        BigDecimal transf = transactionRepository.totalByType(TransactionType.TRANSFERENCIA, range.from, range.to);

        return new ReportSummaryResponse(
                from,
                toInclusive,
                count,
                total,
                cambio,
                entrada,
                saida,
                transf
        );
    }

    private static DateRange range(LocalDate from, LocalDate toInclusive) {
        if (from == null || toInclusive == null) {
            throw new IllegalArgumentException("from and to are required (YYYY-MM-DD)");
        }
        if (toInclusive.isBefore(from)) {
            throw new IllegalArgumentException("to must be >= from");
        }

        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toExclusive = toInclusive.plusDays(1).atStartOfDay();
        return new DateRange(fromDt, toExclusive);
    }
    private record DateRange(LocalDateTime from, LocalDateTime to) {}

}
