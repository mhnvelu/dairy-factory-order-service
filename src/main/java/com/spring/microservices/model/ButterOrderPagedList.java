package com.spring.microservices.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ButterOrderPagedList extends PageImpl<ButterOrderDto> {
    public ButterOrderPagedList(List<ButterOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public ButterOrderPagedList(List<ButterOrderDto> content) {
        super(content);
    }
}
