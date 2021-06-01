package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.statemachine.ButterOrderStateMachineInterceptorAdapter;
import com.spring.microservices.model.ButterOrderDto;
import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ButterOrderManagerImpl implements ButterOrderManager {

    public static final String BUTTER_ORDER_ID_HEADER = "butter-order-id";

    private final ButterOrderRepository butterOrderRepository;
    private final StateMachineFactory<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachineFactory;
    private final ButterOrderStateMachineInterceptorAdapter butterOrderStateMachineInterceptorAdapter;

    @Override
    public ButterOrder newButterOrder(ButterOrder butterOrder) {

        butterOrder.setId(null);
        butterOrder.setOrderStatus(ButterOrderStatusEnum.NEW);
        ButterOrder savedButterOrder = butterOrderRepository.saveAndFlush(butterOrder);
        sendButterOrderEvent(savedButterOrder, ButterOrderEventEnum.VALIDATE_ORDER);
        return savedButterOrder;
    }

    @Transactional
    @Override
    public void processValidateButterOrderResponseEvent(UUID orderId, boolean valid) {
        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(orderId);
        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            if (valid) {
                sendButterOrderEvent(butterOrder, ButterOrderEventEnum.VALIDATION_PASSED);
                ButterOrder validatedButterOrder = butterOrderRepository.findById(orderId).get();
                sendButterOrderEvent(validatedButterOrder, ButterOrderEventEnum.ALLOCATE_ORDER);
            } else {
                sendButterOrderEvent(butterOrder, ButterOrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error("Order Id Not Found : " + orderId));
    }

    @Override
    public void butterOrderAllocationPassed(ButterOrderDto butterOrderDto) {
        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(butterOrderDto.getId());

        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(butterOrder.getId(), ButterOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(butterOrderDto);
        }, () -> log.error("Order Id Not Found: " + butterOrderDto.getId()));
    }

    @Override
    public void butterOrderAllocationPendingInventory(ButterOrderDto butterOrderDto) {
        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(butterOrderDto.getId());

        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(butterOrder.getId(), ButterOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQty(butterOrderDto);
        }, () -> log.error("Order Id Not Found: " + butterOrderDto.getId()));

    }

    @Override
    public void butterOrderAllocationFailed(ButterOrderDto butterOrderDto) {

        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(butterOrderDto.getId());

        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + butterOrderDto.getId()));

    }

    @Override
    public void butterOrderPickedUp(UUID orderId) {
        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(orderId);
        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.BUTTER_ORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + orderId));
    }

    @Override
    public void cancelOrder(UUID orderId) {
        Optional<ButterOrder> butterOrderOptional = butterOrderRepository.findById(orderId);
        butterOrderOptional.ifPresentOrElse(butterOrder -> {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not Found. Id: " + orderId));
    }

    @Transactional
    @Async
    protected void sendButterOrderEvent(ButterOrder butterOrder, ButterOrderEventEnum butterOrderEventEnum) {

        StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachine = buildStateMachine(butterOrder);
        Message message = MessageBuilder.withPayload(butterOrderEventEnum).setHeader(BUTTER_ORDER_ID_HEADER,
                                                                                     butterOrder.getId()).build();
        stateMachine.sendEvent(message);

    }

    private StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> buildStateMachine(ButterOrder butterOrder) {

        StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachine =
                stateMachineFactory.getStateMachine(butterOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(butterOrderStateMachineInterceptorAdapter);
            sma.resetStateMachine(new DefaultStateMachineContext<>(butterOrder.getOrderStatus(), null, null, null));
        });

        stateMachine.start();

        return stateMachine;
    }

    private void updateAllocatedQty(ButterOrderDto butterOrderDto) {
        Optional<ButterOrder> allocatedOrderOptional = butterOrderRepository.findById(butterOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getButterOrderLines().forEach(butterOrderLine -> {
                butterOrderDto.getButterOrderLines().forEach(butterOrderLineDto -> {
                    if (butterOrderLine.getId().equals(butterOrderLineDto.getId())) {
                        butterOrderLine.setQuantityAllocated(butterOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            butterOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + butterOrderDto.getId()));
    }


    private void awaitForStatus(UUID butterOrderId, ButterOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            butterOrderRepository.findById(butterOrderId).ifPresentOrElse(butterOrder -> {
                if (butterOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " +
                              butterOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

}
