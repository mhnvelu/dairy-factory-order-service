package com.spring.microservices.dairyfactoryorderservice.services.listeners;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManager;
import com.spring.microservices.model.events.AllocateButterOrderResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AllocateButterOrderResponseEventListener {

    private final ButterOrderManager butterOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listenAllocateButterOrderResponseEvent(AllocateButterOrderResponseEvent allocateButterOrderResponseEvent) {
        final UUID orderId = allocateButterOrderResponseEvent.getButterOrderDto().getId();
        log.info("Received AllocateButterOrderResponseEvent for Order : " + orderId.toString());

        if (!allocateButterOrderResponseEvent.isAllocationError() && !allocateButterOrderResponseEvent.isPendingInventory()) {
            butterOrderManager.butterOrderAllocationPassed(allocateButterOrderResponseEvent.getButterOrderDto());
        } else if (!allocateButterOrderResponseEvent.isAllocationError() &&
                   allocateButterOrderResponseEvent.isPendingInventory()) {
            butterOrderManager.butterOrderAllocationPendingInventory(allocateButterOrderResponseEvent.getButterOrderDto());
        } else {
            butterOrderManager.butterOrderAllocationFailed(allocateButterOrderResponseEvent.getButterOrderDto());

        }
    }
}
