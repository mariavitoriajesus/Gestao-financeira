package com.beca.financial.transaction_processor.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    public static final String TX_REQUESTED = "transaction.requested";
    public static final String TX_REQUESTED_DTL = "transaction.requested.DLT";

    @Bean
    public NewTopic txRequestedTopic() {
        return TopicBuilder.name(TX_REQUESTED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic txRequestedDltTopic() {
        return TopicBuilder.name(TX_REQUESTED_DTL)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
