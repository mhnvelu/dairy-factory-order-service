package com.spring.microservices.dairyfactoryorderservice.services;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderStatusEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ButterOrderManagerImpl implements ButterOrderManager {

    private final ButterOrderRepository butterOrderRepository;
    private final StateMachineFactory<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachineFactory;

    @Transactional
    @Override
    public ButterOrder newButterOrder(ButterOrder butterOrder) {

        butterOrder.setId(null);
        butterOrder.setOrderStatus(ButterOrderStatusEnum.NEW);
        ButterOrder savedButterOrder = butterOrderRepository.save(butterOrder);
        sendButterOrderEvent(savedButterOrder, ButterOrderEventEnum.VALIDATE_ORDER);
        return savedButterOrder;
    }

    private void sendButterOrderEvent(ButterOrder butterOrder, ButterOrderEventEnum butterOrderEventEnum) {

        StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachine = buildStateMachine(butterOrder);
        Message message = MessageBuilder.withPayload(butterOrderEventEnum).build();
        stateMachine.sendEvent(message);

    }

    private StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> buildStateMachine(ButterOrder butterOrder) {

        StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachine =
                stateMachineFactory.getStateMachine(butterOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.resetStateMachine(new DefaultStateMachineContext<>(butterOrder.getOrderStatus(), null, null, null));
        });

        stateMachine.start();

        return stateMachine;
    }
}
