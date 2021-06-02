package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);
}
