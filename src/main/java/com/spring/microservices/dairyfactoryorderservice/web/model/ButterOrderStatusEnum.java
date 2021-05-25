package com.spring.microservices.dairyfactoryorderservice.web.model;

public enum ButterOrderStatusEnum {
    NEW, VALIDATED, VALIDATION_EXCEPTION, ALLOCATED, ALLOCATION_ERROR, PENDING_INVENTORY,
    PICKED_UP, DELIVERED, DELIVERY_EXCEPTION, CANCELLED
}
