package com.healthcare.payments.config;

import com.healthcare.common.dto.PaymentRequest;
import com.healthcare.payments.service.PaymentProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.consumer.concurrency:3}")
    private int concurrency;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRequest> kafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        return factory;
    }
}
