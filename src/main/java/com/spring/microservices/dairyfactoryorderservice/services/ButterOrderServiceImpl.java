package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.repositories.CustomerRepository;
import com.spring.microservices.dairyfactoryorderservice.web.mappers.ButterOrderMapper;
import com.spring.microservices.model.ButterOrderDto;
import com.spring.microservices.model.ButterOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ButterOrderServiceImpl implements ButterOrderService {

    private final ButterOrderRepository butterOrderRepository;
    private final CustomerRepository customerRepository;
    private final ButterOrderMapper butterOrderMapper;
    private final ApplicationEventPublisher publisher;

    public ButterOrderServiceImpl(ButterOrderRepository butterOrderRepository,
                                  CustomerRepository customerRepository,
                                  ButterOrderMapper butterOrderMapper, ApplicationEventPublisher publisher) {
        this.butterOrderRepository = butterOrderRepository;
        this.customerRepository = customerRepository;
        this.butterOrderMapper = butterOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public ButterOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<ButterOrder> butterOrderPage =
                    butterOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new ButterOrderPagedList(butterOrderPage
                                                    .stream()
                                                    .map(butterOrderMapper::butterOrderToDto)
                                                    .collect(Collectors.toList()), PageRequest.of(
                    butterOrderPage.getPageable().getPageNumber(),
                    butterOrderPage.getPageable().getPageSize()),
                                            butterOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public ButterOrderDto placeOrder(UUID customerId, ButterOrderDto butterOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            ButterOrder butterOrder = butterOrderMapper.dtoToButterOrder(butterOrderDto);
            butterOrder.setId(null); //should not be set by outside client
            butterOrder.setCustomer(customerOptional.get());
            butterOrder.setOrderStatus(ButterOrderStatusEnum.NEW);

            butterOrder.getButterOrderLines().forEach(line -> line.setButterOrder(butterOrder));

            ButterOrder savedButterOrder = butterOrderRepository.saveAndFlush(butterOrder);

            log.debug("Saved Butter Order: " + savedButterOrder.getId());

            return butterOrderMapper.butterOrderToDto(savedButterOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public ButterOrderDto getOrderById(UUID customerId, UUID orderId) {
        return butterOrderMapper.butterOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        ButterOrder butterOrder = getOrder(customerId, orderId);
        butterOrder.setOrderStatus(ButterOrderStatusEnum.PICKED_UP);
        butterOrderRepository.save(butterOrder);
    }

    private ButterOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(orderId);

            if (butterOrderOptional.isPresent()) {
                ButterOrder butterOrder = butterOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if (butterOrder.getCustomer().getId().equals(customerId)) {
                    return butterOrder;
                }
            }
            throw new RuntimeException("Butter Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
