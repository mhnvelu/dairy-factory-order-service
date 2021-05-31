package com.spring.microservices.dairyfactoryorderservice.services.listeners;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManager;
import com.spring.microservices.model.events.ValidateButterOrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateButterOrderResponseEventListener {

    private final ButterOrderManager butterOrderManager;

    @JmsListener(destination = JmsConfig.BUTTER_ORDER_VALIDATE_RESPONSE_QUEUE)
    public void listenValidateButterOrderResponseEvent(ValidateButterOrderResponseEvent validateButterOrderResponseEvent) {
        final UUID orderId = validateButterOrderResponseEvent.getOrderId();
        log.info("Received ValidateButterOrderResponseEvent for Order : " + orderId);
        butterOrderManager.processValidateButterOrderResponseEvent(orderId, validateButterOrderResponseEvent.isValid());
    }


}
