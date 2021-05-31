package com.spring.microservices.model.events;

import com.spring.microservices.model.ButterOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ValidateButterOrderRequestEvent {

    private ButterOrderDto butterOrderDto;
}
