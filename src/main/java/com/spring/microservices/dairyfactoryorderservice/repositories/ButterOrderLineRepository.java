package com.spring.microservices.dairyfactoryorderservice.repositories;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ButterOrderLineRepository extends PagingAndSortingRepository<ButterOrderLine, UUID> {
}
