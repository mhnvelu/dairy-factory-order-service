package com.spring.microservices.dairyfactoryorderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String BUTTER_ORDER_VALIDATE_REQUEST_QUEUE = "validate-order-request-queue";
    public static final String BUTTER_ORDER_VALIDATE_RESPONSE_QUEUE = "validate-order-response-queue";
    public static final String ALLOCATE_ORDER_REQUEST_QUEUE = "allocate-order-request-queue";
    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate-order-response-queue";
    public static final String ALLOCATE_ORDER_FAILURE_QUEUE = "allocate-order-failure-queue";

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}