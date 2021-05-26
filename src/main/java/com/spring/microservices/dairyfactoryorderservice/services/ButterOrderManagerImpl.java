package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.statemachine.ButterOrderStateMachineInterceptorAdapter;
import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ButterOrderManagerImpl implements ButterOrderManager {

    public static final String BUTTER_ORDER_ID_HEADER = "butter-order-id";

    private final ButterOrderRepository butterOrderRepository;
    private final StateMachineFactory<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachineFactory;
    private final ButterOrderStateMachineInterceptorAdapter butterOrderStateMachineInterceptorAdapter;

    @Transactional
    @Override
    public ButterOrder newButterOrder(ButterOrder butterOrder) {

        butterOrder.setId(null);
        butterOrder.setOrderStatus(ButterOrderStatusEnum.NEW);
        ButterOrder savedButterOrder = butterOrderRepository.save(butterOrder);
        sendButterOrderEvent(savedButterOrder, ButterOrderEventEnum.VALIDATE_ORDER);
        return savedButterOrder;
    }

    @Override
    public void processValidateButterOrderResponseEvent(UUID orderId, boolean valid) {
        ButterOrder butterOrder = butterOrderRepository.getOne(orderId);
        if (valid) {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.VALIDATION_PASSED);
            ButterOrder validatedButterOrder = butterOrderRepository.getOne(orderId);
            sendButterOrderEvent(validatedButterOrder, ButterOrderEventEnum.ALLOCATE_ORDER);
        } else {
            sendButterOrderEvent(butterOrder, ButterOrderEventEnum.VALIDATION_FAILED);
        }
    }

    private void sendButterOrderEvent(ButterOrder butterOrder, ButterOrderEventEnum butterOrderEventEnum) {

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
}
