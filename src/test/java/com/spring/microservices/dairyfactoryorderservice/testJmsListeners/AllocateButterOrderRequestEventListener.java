package com.spring.microservices.dairyfactoryorderservice.testJmsListeners;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.model.events.AllocateButterOrderRequestEvent;
import com.spring.microservices.model.events.AllocateButterOrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateButterOrderRequestEventListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_REQUEST_QUEUE)
    public void listenAllocateButterOrderRequestEvent(AllocateButterOrderRequestEvent allocateButterOrderRequestEvent) {
        String orderId = allocateButterOrderRequestEvent.getButterOrderDto().getId().toString();
        log.info("Received AllocateButterOrderRequestEvent for order : " + orderId);

        allocateButterOrderRequestEvent.getButterOrderDto().getButterOrderLines().forEach(butterOrderLineDto -> {
            butterOrderLineDto.setQuantityAllocated(butterOrderLineDto.getOrderQuantity());
        });

        AllocateButterOrderResponseEvent.AllocateButterOrderResponseEventBuilder allocateButterOrderResponseEventBuilder =
                AllocateButterOrderResponseEvent.builder().butterOrderDto(allocateButterOrderRequestEvent.getButterOrderDto())
                        .pendingInventory(false).allocationError(false);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, allocateButterOrderResponseEventBuilder.build());

    }
}
