package com.spring.microservices.dairyfactoryorderservice.web.mappers;

import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDto customerDto);

    CustomerDto customerToCustomerDto(Customer customer);
}
