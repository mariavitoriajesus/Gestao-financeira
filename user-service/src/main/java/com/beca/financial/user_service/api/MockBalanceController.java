package com.beca.financial.user_service.api;


import com.beca.financial.user_service.api.dto.MockBalanceResponse;
import com.beca.financial.user_service.service.MockBalanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/mock")
public class MockBalanceController {
    private final MockBalanceService service;

    public MockBalanceController(MockBalanceService service) {
        this.service = service;
    }

    @GetMapping("/balance/{userId}")
    public MockBalanceResponse getBalance(@PathVariable UUID userId) {
        return service.getBalance(userId);
    }
}
