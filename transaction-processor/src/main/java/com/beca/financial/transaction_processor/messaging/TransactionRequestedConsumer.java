package com.beca.financial.transaction_processor.messaging;

import com.beca.financial.transaction_processor.events.TransactionRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
public class TransactionRequestedConsumer {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public TransactionRequestedConsumer(RestTemplate restTemplate, @Value("${transaction.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @KafkaListener(topics = "transaction.requested", groupId = "transaction-processor")
    public void onMessage(TransactionRequestedEvent event) {

        String status = event.amount().doubleValue() <= 1000.0 ? "APPROVED" : "REJECTED";

        URI uri = URI.create(baseUrl + "/transactions/" + event.transactionId() + "/status");

        RequestEntity<Map<String, String>> request = RequestEntity.patch(uri).body(Map.of("status", status));
        restTemplate.exchange(request, Void.class);

    }
}
