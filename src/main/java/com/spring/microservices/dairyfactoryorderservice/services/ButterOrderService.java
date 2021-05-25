package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.model.ButterOrderDto;
import com.spring.microservices.model.ButterOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ButterOrderService {
    ButterOrderPagedList listOrders(UUID customerId, Pageable pageable);

    ButterOrderDto placeOrder(UUID customerId, ButterOrderDto butterOrderDto);

    ButterOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
