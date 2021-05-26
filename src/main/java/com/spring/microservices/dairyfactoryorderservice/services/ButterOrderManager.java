package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;

import java.util.UUID;

public interface ButterOrderManager {

    ButterOrder newButterOrder(ButterOrder butterOrder);

    void processValidateButterOrderResponseEvent(UUID orderId, boolean valid);
}
