package com.spring.microservices.dairyfactoryorderservice.web.model;

import com.spring.microservices.dairyfactoryorderservice.services.butter.ButterFlavourEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ButterOrderLineDto extends BaseItem {

    private String upc;
    private String butterName;
    private UUID butterId;
    private ButterFlavourEnum flavour;
    private BigDecimal price;
    private Integer orderQuantity = 0;

    @Builder
    public ButterOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                              String upc, String butterName, ButterFlavourEnum flavour, BigDecimal price, UUID butterId, Integer orderQuantity) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.butterName = butterName;
        this.butterId = butterId;
        this.price = price;
        this.flavour = flavour;
        this.orderQuantity = orderQuantity;
    }
}
