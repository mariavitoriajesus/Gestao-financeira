package com.beca.financial.transaction_processor.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaDqlConfig {

    public static final String TX_REQUESTED_TOPIC = "transaction.requested";
    public static final String TX_REQUESTED_DLT = "transaction.requested.DLT";

    @Bean
    public NewTopic txRequestedTopic() {
        return TopicBuilder.name(TX_REQUESTED_TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic txRequestedDltTopic() {
        return TopicBuilder.name(TX_REQUESTED_DLT).partitions(1).replicas(1).build();
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<Object, Object> template) {
        return new DeadLetterPublishingRecoverer(
                template, (record, ex) ->
                new TopicPartition(record.topic() + ".DLT", record.partition())
        );
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));
        return handler;
    }

    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(ConsumerFactory<Object, Object> consumerFactory, DefaultErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }
}
