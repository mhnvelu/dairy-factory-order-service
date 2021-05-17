package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.bootstrap.ButterOrderBootStrap;
import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.repositories.CustomerRepository;
import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderDto;
import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ButterOrderScheduler {

    private final CustomerRepository customerRepository;
    private final ButterOrderService butterOrderService;
    private final ButterOrderRepository butterOrderRepository;
    private final List<String> butterUpcs = new ArrayList<>(3);

    private final Map<String, UUID> butterUids = new HashMap<>();

    public ButterOrderScheduler(CustomerRepository customerRepository, ButterOrderService butterOrderService,
                                ButterOrderRepository butterOrderRepository) {
        this.customerRepository = customerRepository;
        this.butterOrderService = butterOrderService;
        this.butterOrderRepository = butterOrderRepository;

        butterUpcs.add(ButterOrderBootStrap.BUTTER_1_UPC);
        butterUpcs.add(ButterOrderBootStrap.BUTTER_2_UPC);
        butterUpcs.add(ButterOrderBootStrap.BUTTER_3_UPC);

        butterUids.put(ButterOrderBootStrap.BUTTER_1_UPC, ButterOrderBootStrap.BUTTER_1_UUID);
        butterUids.put(ButterOrderBootStrap.BUTTER_2_UPC, ButterOrderBootStrap.BUTTER_2_UUID);
        butterUids.put(ButterOrderBootStrap.BUTTER_3_UPC, ButterOrderBootStrap.BUTTER_3_UUID);

    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(ButterOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1) { //should be just one
            log.info("Placing Order by Customer..." + customerList.get(0).getId());
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String butterToOrder = getRandomButterUpc();

        ButterOrderLineDto butterOrderLineDto = ButterOrderLineDto.builder()
                .upc(butterToOrder).butterId(butterUids.get(butterToOrder))
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<ButterOrderLineDto> butterOrderLineDtoList = new ArrayList<>();
        butterOrderLineDtoList.add(butterOrderLineDto);

        ButterOrderDto butterOrderDto = ButterOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .butterOrderLines(butterOrderLineDtoList)
                .build();

        ButterOrderDto savedOrder = butterOrderService.placeOrder(customer.getId(), butterOrderDto);
    }

    private String getRandomButterUpc() {
        return butterUpcs.get(new Random().nextInt(butterUpcs.size() - 0));
    }
}
