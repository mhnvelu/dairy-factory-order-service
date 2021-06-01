package com.spring.microservices.dairyfactoryorderservice.testJmsListeners;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.model.events.ValidateButterOrderRequestEvent;
import com.spring.microservices.model.events.ValidateButterOrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateButterOrderRequestEventListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.BUTTER_ORDER_VALIDATE_REQUEST_QUEUE)
    public void listenValidationButterOrderRequest(ValidateButterOrderRequestEvent validateButterOrderRequestEvent) {
        boolean isValid = true;

        if (validateButterOrderRequestEvent.getButterOrderDto().getCustomerRef() != null &&
            validateButterOrderRequestEvent.getButterOrderDto().getCustomerRef().equals("fail-validation")) {
            isValid = false;
        }
        log.info("Received ValidateButterOrderRequestEvent order Id : " +
                 validateButterOrderRequestEvent.getButterOrderDto().getId());

        jmsTemplate.convertAndSend(JmsConfig.BUTTER_ORDER_VALIDATE_RESPONSE_QUEUE,
                                   ValidateButterOrderResponseEvent.builder().isValid(isValid)
                                           .orderId(validateButterOrderRequestEvent.getButterOrderDto().getId()).build());
    }
}
