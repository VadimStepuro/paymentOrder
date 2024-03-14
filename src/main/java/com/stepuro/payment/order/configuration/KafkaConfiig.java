package com.stepuro.payment.order.configuration;

import com.stepuro.payment.order.api.dto.PaymentOrderEntityDto;
import com.stepuro.payment.order.api.exceptions.ClientException;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Log4j2
public class KafkaConfiig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.group.id}")
    private String kafkaGroupId;

    @Value(value = "${kafka.back-off.interval}")
    private Long backOffInterval;

    @Value(value = "${kafka.back-off.max-attempts}")
    private Long backOffMaxAttempts;

    @Bean
    public ConsumerFactory<String, PaymentOrderEntityDto> createPaymentConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                kafkaGroupId);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(
                JsonDeserializer.TYPE_MAPPINGS,
                "payment:com.stepuro.payment.order.api.dto.PaymentOrderEntityDto");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(PaymentOrderEntityDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentOrderEntityDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentOrderEntityDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(createPaymentConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(backOffInterval, backOffMaxAttempts);

        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler((consumerRecord, exception) ->
                log.error("Exception thrown", exception),
                fixedBackOff);

        defaultErrorHandler.addNotRetryableExceptions(ClientException.class);

        return defaultErrorHandler;
    }

}
