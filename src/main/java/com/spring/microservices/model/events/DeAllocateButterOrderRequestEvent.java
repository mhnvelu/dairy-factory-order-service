package com.spring.microservices.model.events;

import com.spring.microservices.model.ButterOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeAllocateButterOrderRequestEvent {
    private ButterOrderDto butterOrderDto;
}
