package com.example.orders.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static com.example.common.events.Topics.*;

@Configuration
public class KafkaConfig {
    @Bean
    ProducerFactory<String, Object> producerFactory(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>(properties.buildProducerProperties(null));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic orderEventsTopic() {
        return TopicBuilder.name(ORDER_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic inventoryCommandsTopic() {
        return TopicBuilder.name(INVENTORY_COMMANDS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic inventoryRepliesTopic() {
        return TopicBuilder.name(INVENTORY_REPLIES).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentCommandsTopic() {
        return TopicBuilder.name(PAYMENT_COMMANDS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentRepliesTopic() {
        return TopicBuilder.name(PAYMENT_REPLIES).partitions(3).replicas(1).build();
    }
}
