package com.spring.microservices.dairyfactoryorderservice.web.mappers;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderLine;
import com.spring.microservices.dairyfactoryorderservice.services.butter.ButterDtoV2;
import com.spring.microservices.dairyfactoryorderservice.services.butter.ButterService;
import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class ButterOrderLineMapperDecorator implements ButterOrderLineMapper {
    private ButterService butterService;
    private ButterOrderLineMapper butterOrderLineMapper;

    @Autowired
    public void setButterService(ButterService butterService) {
        this.butterService = butterService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setButterOrderLineMapper(ButterOrderLineMapper butterOrderLineMapper) {
        this.butterOrderLineMapper = butterOrderLineMapper;
    }

    @Override
    public ButterOrderLineDto butterOrderLineToDto(ButterOrderLine butterOrderLine) {
        ButterOrderLineDto butterOrderLineDto = butterOrderLineMapper.butterOrderLineToDto(butterOrderLine);
        Optional<ButterDtoV2> butterDtoV2Optional = butterService.getButterByUpc(butterOrderLine.getUpc());
        butterDtoV2Optional.ifPresent(butterDtoV2 -> {
            butterOrderLineDto.setFlavour(butterDtoV2.getFlavour());
            butterOrderLineDto.setPrice(butterDtoV2.getPrice());
            butterOrderLineDto.setButterName(butterDtoV2.getName());
            butterOrderLineDto.setButterId(butterDtoV2.getId());
        });
        return butterOrderLineDto;
    }
}
