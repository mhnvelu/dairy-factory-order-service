package com.spring.microservices.dairyfactoryorderservice.statemachine;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderEventEnum;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class ButterOrderStateMachineConfig extends StateMachineConfigurerAdapter<ButterOrderStatusEnum, ButterOrderEventEnum> {
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
}
