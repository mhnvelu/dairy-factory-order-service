package com.spring.microservices.dairyfactoryorderservice.domain;

import com.spring.microservices.model.ButterOrderStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class ButterOrder extends BaseEntity {

    private String customerRef;
    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "butterOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<ButterOrderLine> butterOrderLines;
    private ButterOrderStatusEnum orderStatus = ButterOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;

    @Builder
    public ButterOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef, Customer customer,
                       Set<ButterOrderLine> butterOrderLines, ButterOrderStatusEnum orderStatus,
                       String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.butterOrderLines = butterOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }
}
