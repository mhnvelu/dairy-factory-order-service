package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.dairyfactoryorderservice.repositories.CustomerRepository;
import com.spring.microservices.dairyfactoryorderservice.web.mappers.CustomerMapper;
import com.spring.microservices.model.CustomerDto;
import com.spring.microservices.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {
        Page<Customer> customersPage = customerRepository.findAll(pageable);
        List<CustomerDto> customerDtoList =
                customersPage.stream().map(customerMapper::customerToCustomerDto).collect(Collectors.toList());
        return new CustomerPagedList(customerDtoList, PageRequest.of(customersPage.getPageable().getPageNumber(),
                                                                     customersPage.getPageable().getPageSize()),
                                     customersPage.getTotalElements());
    }
}
