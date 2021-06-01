package com.spring.microservices.dairyfactoryorderservice.statemachine.actions;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManagerImpl;
import com.spring.microservices.model.ButterOrderStatusEnum;
import com.spring.microservices.model.events.AllocateButterOrderFailureEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocateFailureButterOrderAction implements Action<ButterOrderStatusEnum, ButterOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final ButterOrderRepository butterOrderRepository;

    @Override
    public void execute(StateContext<ButterOrderStatusEnum, ButterOrderEventEnum> context) {
        UUID orderId = (UUID) context.getMessageHeaders().getOrDefault(ButterOrderManagerImpl.BUTTER_ORDER_ID_HEADER, "");
        log.error("Compensating Transactions... Allocate Failure for order id : " + orderId);
        ButterOrder butterOrder = butterOrderRepository.findById(orderId).get();
        log.info("Sending AllocateButterOrderFailureEvent to ALLOCATE_ORDER_FAILURE_QUEUE for Butter Order : " + orderId);
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_FAILURE_QUEUE,
                                   AllocateButterOrderFailureEvent.builder().orderId(orderId)
                                           .build());
        log.info("Sent AllocateButterOrderFailureEvent to ALLOCATE_ORDER_FAILURE_QUEUE for Butter Order : " + orderId);
    }
}
