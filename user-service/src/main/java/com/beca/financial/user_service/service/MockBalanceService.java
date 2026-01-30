package com.beca.financial.user_service.service;

import com.beca.financial.user_service.api.dto.MockBalanceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class MockBalanceService {

    public MockBalanceResponse getBalance(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        long seed = Math.abs(userId.getMostSignificantBits() ^ userId.getLeastSignificantBits());

        BigDecimal current = BigDecimal.valueOf(seed % 200_00).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN)
                .subtract(BigDecimal.valueOf(500));

        BigDecimal limit = BigDecimal.valueOf(1000);
        BigDecimal avaliable = current.add(limit).max(BigDecimal.ZERO);

        String status = (seed % 20 == 0) ? "BLOCKED" : "ACTIVE";

        return new MockBalanceResponse(
                userId,
                "BRL",
                current.setScale(2, RoundingMode.HALF_UP),
                avaliable.setScale(2, RoundingMode.HALF_UP),
                limit.setScale(2, RoundingMode.HALF_UP),
                status
        );
    }
}
