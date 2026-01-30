package com.beca.financial.transaction_api.repository;

import com.beca.financial.transaction_api.domain.Transaction;
import com.beca.financial.transaction_api.domain.enums.TransactionType;
import com.beca.financial.transaction_api.dto.ReportByTypeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
        select new com.beca.financial.transaction_api.dto.ReportByTypeItem(
            t.type,
            count(t),
            coalesce(sum(t.amount), 0)
        )
        from Transaction t
        where t.createAt >= :from and t.createAt < :to
        group by t.type
        order by t.type
    """)
    List<ReportByTypeItem> reportByType(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.createAt >= :from and t.createAt < :to
    """)
    BigDecimal totalAmount(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        select count(t)
        from Transaction t
        where t.createAt >= :from and t.createAt < :to
    """)
    long totalCount(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.type = :type and t.createAt >= :from and t.createAt < :to
    """)
    BigDecimal totalByType(
            @Param("type") TransactionType type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
