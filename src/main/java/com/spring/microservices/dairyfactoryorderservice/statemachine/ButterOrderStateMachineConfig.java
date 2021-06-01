package com.spring.microservices.dairyfactoryorderservice.statemachine;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.statemachine.actions.*;
import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class ButterOrderStateMachineConfig extends StateMachineConfigurerAdapter<ButterOrderStatusEnum, ButterOrderEventEnum> {

    private final ValidateButterOrderAction validateButterOrderAction;
    private final AllocateButterOrderAction allocateButterOrderAction;
    private final ValidateFailureButterOrderAction validateFailureButterOrderAction;
    private final AllocateFailureButterOrderAction allocateFailureButterOrderAction;
    private final DeAllocateButterOrderAction deAllocateButterOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<ButterOrderStatusEnum, ButterOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(ButterOrderStatusEnum.NEW)
                .states(EnumSet.allOf(ButterOrderStatusEnum.class))
                .end(ButterOrderStatusEnum.PICKED_UP)
                .end(ButterOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(ButterOrderStatusEnum.ALLOCATION_ERROR)
                .end(ButterOrderStatusEnum.DELIVERED)
                .end(ButterOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(ButterOrderStatusEnum.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ButterOrderStatusEnum, ButterOrderEventEnum> transitions)
            throws Exception {
        transitions.withExternal().source(ButterOrderStatusEnum.NEW).target(ButterOrderStatusEnum.VALIDATION_PENDING)
                .event(ButterOrderEventEnum.VALIDATE_ORDER).action(validateButterOrderAction)
                .and().withExternal().source(ButterOrderStatusEnum.VALIDATION_PENDING).target(ButterOrderStatusEnum.VALIDATED)
                    .event(ButterOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal().source(ButterOrderStatusEnum.VALIDATION_PENDING).target(ButterOrderStatusEnum.CANCELLED)
                    .event(ButterOrderEventEnum.CANCEL_ORDER)
                .and().withExternal().source(ButterOrderStatusEnum.VALIDATION_PENDING)
                    .target(ButterOrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(ButterOrderEventEnum.VALIDATION_FAILED).action(validateFailureButterOrderAction)
                .and().withExternal().source(ButterOrderStatusEnum.VALIDATED).target(ButterOrderStatusEnum.CANCELLED)
                    .event(ButterOrderEventEnum.CANCEL_ORDER)
                .and().withExternal().source(ButterOrderStatusEnum.VALIDATED).target(ButterOrderStatusEnum.ALLOCATION_PENDING)
                    .event(ButterOrderEventEnum.ALLOCATE_ORDER).action(allocateButterOrderAction)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATION_PENDING).target(ButterOrderStatusEnum.CANCELLED)
                    .event(ButterOrderEventEnum.CANCEL_ORDER)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATION_PENDING).target(ButterOrderStatusEnum.ALLOCATED)
                    .event(ButterOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATED).target(ButterOrderStatusEnum.CANCELLED)
                    .event(ButterOrderEventEnum.CANCEL_ORDER).action(deAllocateButterOrderAction)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATION_PENDING)
                    .target(ButterOrderStatusEnum.ALLOCATION_ERROR)
                    .event(ButterOrderEventEnum.ALLOCATION_FAILED).action(allocateFailureButterOrderAction)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATION_PENDING)
                    .target(ButterOrderStatusEnum.PENDING_INVENTORY)
                    .event(ButterOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal().source(ButterOrderStatusEnum.ALLOCATED).target(ButterOrderStatusEnum.PICKED_UP)
                    .event(ButterOrderEventEnum.BUTTER_ORDER_PICKED_UP);

        ;
    }
}
