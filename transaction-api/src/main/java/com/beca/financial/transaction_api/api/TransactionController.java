package com.beca.financial.transaction_api.api;


import com.beca.financial.transaction_api.dto.CreateTransactionRequest;
import com.beca.financial.transaction_api.dto.CreateTransactionResponse;
import com.beca.financial.transaction_api.dto.TransactionResponse;
import com.beca.financial.transaction_api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreateTransactionResponse create(@RequestBody @Valid CreateTransactionRequest request) {
        UUID id = transactionService.create(request);
        return new CreateTransactionResponse(id);
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }
}
