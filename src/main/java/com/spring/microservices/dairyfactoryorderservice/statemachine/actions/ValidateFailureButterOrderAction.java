package com.spring.microservices.dairyfactoryorderservice.statemachine.actions;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManagerImpl;
import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateFailureButterOrderAction implements Action<ButterOrderStatusEnum, ButterOrderEventEnum> {
    @Override
    public void execute(StateContext<ButterOrderStatusEnum, ButterOrderEventEnum> context) {

        UUID orderId = (UUID) context.getMessageHeaders().getOrDefault(ButterOrderManagerImpl.BUTTER_ORDER_ID_HEADER, "");
        log.error("Compensating Transactions... Validation Failure for order id : " + orderId);
    }
}
