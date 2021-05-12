package com.spring.microservices.dairyfactoryorderservice.web.mappers;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderLine;
import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(ButterOrderLineMapperDecorator.class)
public interface ButterOrderLineMapper {
    ButterOrderLineDto butterOrderLineToDto(ButterOrderLine butterOrderLine);

    ButterOrderLine dtoToButterOrderLine(ButterOrderLineDto butterOrderLineDto);
}
