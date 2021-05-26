package com.spring.microservices.model.events;

import com.spring.microservices.model.ButterOrderDto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Builder
public class AllocateButterOrderRequestEvent {

    private final ButterOrderDto butterOrderDto;

}
