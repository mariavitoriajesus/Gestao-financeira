package com.beca.financial.transaction_processor.messaging;

import com.beca.financial.transaction_processor.events.TransactionRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TransactionRequestedConsumer {
    private final RestClient transactionApiCliente;

    public TransactionRequestedConsumer(RestClient restClient) {
        this.transactionApiCliente = restClient;
    }

    @KafkaListener(topics = "transaction.requested", groupId = "transaction-processor")
    public void onMessage(TransactionRequestedEvent event) {
        String status = event.amount().doubleValue() <= 1000.0 ? "APRPROVED" : "FAILED";

        try {
            transactionApiCliente.patch()
                    .uri("/transaction/{id}/status", event.transactionId())
                    .body(Map.of("status", status))
                    .retrieve().toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
