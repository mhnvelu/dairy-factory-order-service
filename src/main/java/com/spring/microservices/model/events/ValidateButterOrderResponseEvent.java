package com.spring.microservices.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ValidateButterOrderResponseEvent {

    private UUID orderId;
    private boolean isValid;

}
