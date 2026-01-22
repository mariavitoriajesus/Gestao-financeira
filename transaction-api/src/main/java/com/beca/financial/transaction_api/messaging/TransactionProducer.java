package com.beca.financial.transaction_api.messaging;

import com.beca.financial.transaction_api.events.TransactionRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionProducer {

    public static final String TOPIC = "transaction.requested";

    private final KafkaTemplate<String, TransactionRequestedEvent> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, TransactionRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionRequestedEvent event) {
        kafkaTemplate.send(TOPIC, event.transactionId().toString(), event);
    }
}
