package com.spring.microservices.dairyfactoryorderservice.domain;

public enum ButterOrderEventEnum {

    VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED, ALLOCATION_SUCCESS,
    ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED, BUTTER_ORDER_PICKED_UP, CANCEL_ORDER
    
}
