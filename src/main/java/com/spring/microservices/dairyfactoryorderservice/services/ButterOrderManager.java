package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.model.ButterOrderDto;

import java.util.UUID;

public interface ButterOrderManager {

    ButterOrder newButterOrder(ButterOrder butterOrder);

    void processValidateButterOrderResponseEvent(UUID orderId, boolean valid);

    void butterOrderAllocationPassed(ButterOrderDto butterOrderDto);

    void butterOrderAllocationPendingInventory(ButterOrderDto butterOrderDto);

    void butterOrderAllocationFailed(ButterOrderDto butterOrderDto);

    void butterOrderPickedUp(UUID orderId);
}
