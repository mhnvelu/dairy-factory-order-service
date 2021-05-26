package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;

public interface ButterOrderManager {

    ButterOrder newButterOrder(ButterOrder butterOrder);

}
