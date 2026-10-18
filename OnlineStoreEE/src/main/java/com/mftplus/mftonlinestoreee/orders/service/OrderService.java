package com.mftplus.mftonlinestoreee.orders.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.orders.dto.OrderDto;
import com.mftplus.mftonlinestoreee.orders.dto.OrderRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService extends BaseService<OrderRequest, OrderDto>{
    List<OrderDto> findByCustomer(Long customerId);

    Optional<OrderDto> confirm(Long id);

    Optional<OrderDto> cancel(Long id);

    Optional<OrderDto> ship(Long id);

    Optional<OrderDto> deliver(Long id);
}
