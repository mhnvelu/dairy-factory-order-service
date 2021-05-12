package com.spring.microservices.dairyfactoryorderservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ButterOrderLine extends BaseEntity {

    @ManyToOne
    private ButterOrder butterOrder;
    private UUID butterId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
    private String upc;

    @Builder
    public ButterOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                           ButterOrder butterOrder, UUID butterId, String upc, Integer orderQuantity,
                           Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.butterOrder = butterOrder;
        this.butterId = butterId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}
