package com.spring.microservices.dairyfactoryorderservice.web.mappers;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, ButterOrderLineMapper.class})
public interface ButterOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    ButterOrderDto butterOrderToDto(ButterOrder butterOrder);

    ButterOrder dtoToButterOrder(ButterOrderDto dto);
}
