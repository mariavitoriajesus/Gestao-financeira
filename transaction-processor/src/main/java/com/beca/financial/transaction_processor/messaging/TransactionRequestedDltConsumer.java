package com.beca.financial.transaction_processor.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionRequestedDltConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransactionRequestedDltConsumer.class);

    @KafkaListener(topics = "transaction.requested.DLT", groupId = "transaction-processor-dlt")
    public void onDltMessage(ConsumerRecord<String, Object> record) {
        log.error("DLT message received. topic={} partition={} offset={} key={} value={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
