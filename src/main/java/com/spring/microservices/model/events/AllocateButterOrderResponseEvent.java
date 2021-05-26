package com.spring.microservices.model.events;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AllocateButterOrderResponseEvent {

    private UUID orderId;
    private boolean allocated;
}
