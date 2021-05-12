package com.spring.microservices.dairyfactoryorderservice.services.butter;

import com.spring.microservices.dairyfactoryorderservice.web.model.ButterOrderDto;

import java.util.Optional;
import java.util.UUID;

public interface ButterService {

    Optional<ButterDtoV2> getButterById(UUID uuid);
    Optional<ButterDtoV2> getButterByUpc(String upc);

}
