package com.spring.microservices.dairyfactoryorderservice.statemachine.actions;

import com.spring.microservices.dairyfactoryorderservice.config.JmsConfig;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManagerImpl;
import com.spring.microservices.dairyfactoryorderservice.web.mappers.ButterOrderMapper;
import com.spring.microservices.model.ButterOrderStatusEnum;
import com.spring.microservices.model.events.ValidateButterOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

/*
ValidateButterOrderEvent is send to queue BUTTER_ORDER_VALIDATE_QUEUE. Inventory service has to receive and process it.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateButterOrderAction implements Action<ButterOrderStatusEnum, ButterOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final ButterOrderMapper butterOrderMapper;
    private final ButterOrderRepository butterOrderRepository;

    @Override
    public void execute(StateContext<ButterOrderStatusEnum, ButterOrderEventEnum> context) {
        String orderId = (String) context.getMessageHeaders().getOrDefault(ButterOrderManagerImpl.BUTTER_ORDER_ID_HEADER, "");
        ButterOrder butterOrder = butterOrderRepository.getOne(UUID.fromString(orderId));
        log.info("Sending ValidateButterOrderEvent to BUTTER_ORDER_VALIDATE_QUEUE for Butter Order : " + orderId);
        jmsTemplate.convertAndSend(JmsConfig.BUTTER_ORDER_VALIDATE_QUEUE,
                                   ValidateButterOrderEvent.builder()
                                           .butterOrderDto(butterOrderMapper.butterOrderToDto(butterOrder)).build());
        log.info("Sent ValidateButterOrderEvent to BUTTER_ORDER_VALIDATE_QUEUE for Butter Order : " + orderId);
    }
}