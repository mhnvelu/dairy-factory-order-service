package com.spring.microservices.dairyfactoryorderservice.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ButterOrderDto extends BaseItem {

    private UUID customerId;
    private String customerRef;
    private List<ButterOrderLineDto> butterOrderLines;
    private OrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;

    @Builder
    public ButterOrderDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, UUID customerId, List<ButterOrderLineDto> butterOrderLines,
                          OrderStatusEnum orderStatus, String orderStatusCallbackUrl, String customerRef) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerId = customerId;
        this.butterOrderLines = butterOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.customerRef = customerRef;
    }
}
