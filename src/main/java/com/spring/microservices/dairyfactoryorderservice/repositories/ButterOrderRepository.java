package com.spring.microservices.dairyfactoryorderservice.repositories;

import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.model.ButterOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface ButterOrderRepository extends JpaRepository<ButterOrder, UUID> {

    Page<ButterOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<ButterOrder> findAllByOrderStatus(ButterOrderStatusEnum butterOrderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ButterOrder findOneById(UUID id);
}
