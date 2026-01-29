package com.beca.financial.transaction_api.service;

import com.beca.financial.transaction_api.domain.ExchangeTransaction;
import com.beca.financial.transaction_api.domain.Transaction;
import com.beca.financial.transaction_api.domain.enums.TransactionStatus;
import com.beca.financial.transaction_api.domain.enums.TransactionType;
import com.beca.financial.transaction_api.dto.CreateTransactionRequest;
import com.beca.financial.transaction_api.dto.ExchangeRateResponse;
import com.beca.financial.transaction_api.dto.TransactionResponse;
import com.beca.financial.transaction_api.events.TransactionRequestedEvent;
import com.beca.financial.transaction_api.messaging.BrasilApiExchangeClient;
import com.beca.financial.transaction_api.messaging.TransactionProducer;
import com.beca.financial.transaction_api.repository.ExchangeTransactionRepository;
import com.beca.financial.transaction_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ExchangeTransactionRepository exchangeTransactionRepository;
    private final BrasilApiExchangeClient brasilApiExchangeClient;
    private final TransactionProducer producer;

    public TransactionService(TransactionRepository transactionRepository, ExchangeTransactionRepository exchangeTransactionRepository, BrasilApiExchangeClient brasilApiExchangeClient, TransactionProducer producer) {
        this.transactionRepository = transactionRepository;
        this.exchangeTransactionRepository = exchangeTransactionRepository;
        this.brasilApiExchangeClient = brasilApiExchangeClient;
        this.producer = producer;
    }

    @Transactional
    public UUID create(CreateTransactionRequest request) {

        if (request.type() == null) throw new IllegalArgumentException("type is required");
        if (request.userId() == null) throw new IllegalArgumentException("userId is required.");
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount must be greater than 0.");
        }

        Transaction tx = new Transaction();
        tx.setUserId(request.userId());
        tx.setType(request.type());
        tx.setStatus(TransactionStatus.PENDING);
        tx.setCreateAt(LocalDateTime.now()); // ✅ garante que não fica null

        if (request.type() == TransactionType.CAMBIO) {
            validateExchangeRequest(request);

            String currencyFrom = request.currencyFrom().trim().toUpperCase();
            String currencyTo = request.currencyTo().trim().toUpperCase();

            LocalDate quoteDate = (request.quoteDate() != null)
                    ? lastBusinessDay(request.quoteDate())
                    : lastBusinessDay(LocalDate.now().minusDays(1));

            BigDecimal rate = brasilApiExchangeClient.getExchangeRate(currencyFrom, quoteDate);
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid exchange rate returned by the API.");
            }

            BigDecimal amountFrom = request.amount();
            BigDecimal amountTo = amountFrom.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            tx.setAmount(amountFrom);
            tx.setCurrency(currencyTo);
            tx.setDescription(request.description() != null && !request.description().isBlank()
                    ? request.description()
                    : ("Exchange " + currencyFrom + " -> " + currencyTo));

            Transaction savedTx = transactionRepository.save(tx);

            ExchangeTransaction ex = new ExchangeTransaction();
            ex.setTransactionId(savedTx.getId());
            ex.setCurrencyFrom(currencyFrom);
            ex.setCurrencyTo(currencyTo);
            ex.setRate(rate);
            ex.setAmountFrom(amountFrom);
            ex.setAmountTo(amountTo);
            ex.setQuoteDate(quoteDate);
            ex.setSource("API_EXTERNA");
            exchangeTransactionRepository.save(ex);

            TransactionRequestedEvent event = new TransactionRequestedEvent(
                    savedTx.getId(),
                    savedTx.getUserId(),
                    savedTx.getType(),
                    savedTx.getAmount(),
                    savedTx.getCurrency(),
                    savedTx.getDescription(),
                    savedTx.getCreateAt()
            );
            producer.publish(event);

            return savedTx.getId();
        }

        if (request.currency() == null || request.currency().isBlank()) {
            throw new IllegalArgumentException("Currency is required for non-exchange transactions.");
        }

        tx.setAmount(request.amount());
        tx.setCurrency(request.currency().trim().toUpperCase());
        tx.setDescription(request.description());

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

        ExchangeRateResponse exchange = null;

        if (tx.getType() == TransactionType.CAMBIO) {
            ExchangeTransaction ex = exchangeTransactionRepository.findByTransactionId(tx.getId()).orElse(null);

            if (ex != null) {
                exchange = new ExchangeRateResponse(
                        ex.getCurrencyFrom(),
                        ex.getCurrencyTo(),
                        ex.getRate(),
                        ex.getAmountFrom(),
                        ex.getAmountTo(),
                        ex.getQuoteDate(),
                        ex.getSource()
                );
            }
        }

        return new TransactionResponse(
                tx.getId(),
                tx.getUserId(),
                tx.getType(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getDescription(),
                tx.getStatus(),
                tx.getCreateAt(),
                exchange
        );
    }

    private void validateExchangeRequest(CreateTransactionRequest request) {
        if (request.currencyFrom() == null || request.currencyFrom().isBlank()) {
            throw new IllegalArgumentException("currencyFrom is required for EXCHANGE.");
        }
        if (request.currencyTo() == null || request.currencyTo().isBlank()) {
            throw new IllegalArgumentException("currencyTo is required for EXCHANGE");
        }
        if (request.currencyFrom().trim().equalsIgnoreCase(request.currencyTo().trim())) {
            throw new IllegalArgumentException("currencyFrom and currencyTo must be different.");
        }
        if (request.currencyFrom().trim().length() != 3 || request.currencyTo().trim().length() != 3) {
            throw new IllegalArgumentException("currencyFrom/currencyTo must have 3 characters (e.g. USD, BRL)");
        }
    }

    @Transactional
    public void updateStatus(UUID id, TransactionStatus status) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
        tx.setStatus(status);
        transactionRepository.save(tx);
    }

    private LocalDate lastBusinessDay(LocalDate d) {
        if (d.getDayOfWeek() == DayOfWeek.SATURDAY) return d.minusDays(1);
        if (d.getDayOfWeek() == DayOfWeek.SUNDAY) return d.minusDays(2);
        return d;
    }
}
