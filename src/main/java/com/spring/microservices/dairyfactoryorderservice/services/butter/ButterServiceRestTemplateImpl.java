package com.spring.microservices.dairyfactoryorderservice.services.butter;

import com.spring.microservices.model.v2.ButterDtoV2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@ConfigurationProperties(prefix = "dairy.factory", ignoreUnknownFields = false)
@Service
public class ButterServiceRestTemplateImpl implements ButterService {
    public final String BUTTER_PATH_V2 = "/api/v2/butter/";
    public final String BUTTER_UPC_PATH_V2 = "/api/v2/butterUpc/";
    private final RestTemplate restTemplate;
    private String butterServiceHost;

    public ButterServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setButterServiceHost(String butterServiceHost) {
        this.butterServiceHost = butterServiceHost;
    }

    @Override
    public Optional<ButterDtoV2> getButterById(UUID uuid) {
        return Optional.of(restTemplate.getForObject(butterServiceHost + BUTTER_PATH_V2 + uuid.toString(), ButterDtoV2.class));
    }

    @Override
    public Optional<ButterDtoV2> getButterByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(butterServiceHost + BUTTER_UPC_PATH_V2 + upc, ButterDtoV2.class));
    }
}
