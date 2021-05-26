package com.spring.microservices.dairyfactoryorderservice.statemachine;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.services.ButterOrderManagerImpl;
import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ButterOrderStateMachineInterceptorAdapter
        extends StateMachineInterceptorAdapter<ButterOrderStatusEnum, ButterOrderEventEnum> {

    private final ButterOrderRepository butterOrderRepository;

    @Override
    public void preStateChange(State<ButterOrderStatusEnum, ButterOrderEventEnum> state, Message<ButterOrderEventEnum> message,
                               Transition<ButterOrderStatusEnum, ButterOrderEventEnum> transition,
                               StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> stateMachine,
                               StateMachine<ButterOrderStatusEnum, ButterOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(
                    String.class.cast(msg.getHeaders().getOrDefault(ButterOrderManagerImpl.BUTTER_ORDER_ID_HEADER, "")))
                    .ifPresent(orderId -> {
                        log.info("Saving state for Butter Order Id : " + orderId + " State : " + state.getId());
                        ButterOrder butterOrder = butterOrderRepository.getOne(UUID.fromString(orderId));
                        butterOrder.setOrderStatus(state.getId());
                        butterOrderRepository.saveAndFlush(butterOrder);
                    });
        });

    }
}
