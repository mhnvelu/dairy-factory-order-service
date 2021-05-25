package com.spring.microservices.dairyfactoryorderservice.web.controllers;

import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderService;
import com.spring.microservices.model.ButterOrderDto;
import com.spring.microservices.model.ButterOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class ButterOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final ButterOrderService butterOrderService;

    public ButterOrderController(ButterOrderService butterOrderService) {
        this.butterOrderService = butterOrderService;
    }

    @GetMapping("orders")
    public ButterOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                           @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return butterOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ButterOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody ButterOrderDto butterOrderDto) {
        return butterOrderService.placeOrder(customerId, butterOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public ButterOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return butterOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        butterOrderService.pickupOrder(customerId, orderId);
    }
}
