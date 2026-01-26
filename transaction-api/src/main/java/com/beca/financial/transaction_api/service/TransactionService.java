package com.beca.financial.transaction_api.service;

import com.beca.financial.transaction_api.domain.Transaction;
import com.beca.financial.transaction_api.domain.enums.TransactionStatus;
import com.beca.financial.transaction_api.dto.CreateTransactionRequest;
import com.beca.financial.transaction_api.dto.TransactionResponse;
import com.beca.financial.transaction_api.events.TransactionRequestedEvent;
import com.beca.financial.transaction_api.messaging.TransactionProducer;
import com.beca.financial.transaction_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer producer;

    public TransactionService(TransactionRepository transactionRepository, TransactionProducer producer) {
        this.transactionRepository = transactionRepository;
        this.producer = producer;
    }

    @Transactional
    public UUID create(CreateTransactionRequest request) {
        // Observação: validação forte deve estar no Controller com @Valid.
        // Aqui a gente só faz “sanitização”/normalização.

        Transaction tx = new Transaction();
        tx.setUserId(request.userId());
        tx.setType(request.type());
        tx.setAmount(request.amount());

        String currency = request.currency() == null ? null : request.currency().trim().toUpperCase();
        tx.setCurrency(currency);

        tx.setDescription(request.description());
        tx.setStatus(TransactionStatus.PENDING);
        tx.setCreateAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(tx);

        TransactionRequestedEvent event = new TransactionRequestedEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getType(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getDescription(),
                saved.getCreateAt()
        );

        producer.publish(event);

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(UUID id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));

        return toResponse(tx);
    }

    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getUserId(),
                tx.getType(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getDescription(),
                tx.getStatus(),
                tx.getCreateAt()
        );
    }

    @Transactional
    public void updateStatus(UUID id, TransactionStatus status) {
        Transaction tx = transactionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Transaction not found: " + id));
        tx.setStatus(status);
        transactionRepository.save(tx);
    }
}
