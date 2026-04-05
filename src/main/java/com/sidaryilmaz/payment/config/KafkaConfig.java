package com.sidaryilmaz.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentsInitiatedTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENTS_INITIATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentsSettledTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENTS_SETTLED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
