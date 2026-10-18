package com.mftplus.mftonlinestoreee.orders.service.impl;


import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.orders.dto.OrderDto;
import com.mftplus.mftonlinestoreee.orders.dto.OrderRequest;
import com.mftplus.mftonlinestoreee.orders.entity.Order;
import com.mftplus.mftonlinestoreee.orders.entity.OrderItem;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderStatus;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingStatus;
import com.mftplus.mftonlinestoreee.orders.exception.CustomerNotFoundException;
import com.mftplus.mftonlinestoreee.orders.exception.InvalidOrderStatusException;
import com.mftplus.mftonlinestoreee.orders.exception.OrderNotEditableException;
import com.mftplus.mftonlinestoreee.orders.mapper.OrderMapper;
import com.mftplus.mftonlinestoreee.orders.repository.OrderRepository;
import com.mftplus.mftonlinestoreee.orders.service.OrderService;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    @Inject
    OrderRepository repository;
    @Inject
    OrderMapper mapper;
    @Inject
    UserRepository userRepository;


    @Transactional
    @Override
    public OrderDto create(OrderRequest request) {
        loadCustomer(request.customer().getId());
        Order order = mapper.toEntity(request);
        order.setOrderStatus(OrderStatus.NEW);
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setTotal(calculateTotal(order));
        return mapper.toDto(repository.save(order));
    }

    @Override
    public Optional<OrderDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<OrderDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<OrderDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<OrderDto> update(Long id, OrderRequest request) {
        return repository.findById(id)
                .map(order -> {
                    checkEditable(order);
                    mapper.updateEntity(request, order);
                    return mapper.toDto(order);
                });
    }

    @Transactional
    @Override
    public Optional<OrderDto> deleteById(Long id) {
        return repository.findById(id)
                .map(order -> {
                    checkEditable(order);
                    repository.delete(order);
                    return mapper.toDto(order);
                });
    }

    // OrderService additional methods

    @Override
    @Transactional
    public Optional<OrderDto> deliver(Long id) {
        return repository.findById(id)
                .map(order -> {
                    if (order.getShippingStatus() == ShippingStatus.SHIPPED){
                        OrderRequest request = repository.createDeliverRequest(order);
                        update(id, request);
                        order.setShippingStatus(ShippingStatus.DELIVERED);
                    }else throw new InvalidOrderStatusException("orderId: "+ id);
                    return mapper.toDto(order);
                });
    }

    @Override
    @Transactional
    public Optional<OrderDto> ship(Long id) {
        return repository.findById(id)
                .map(order -> {
                    if (order.getOrderStatus() == OrderStatus.CONFIRMED && order.getShippingStatus() == ShippingStatus.PENDING){
                        OrderRequest request = repository.createShipRequest(order);
                        update(id, request);
                        order.setShippingStatus(ShippingStatus.SHIPPED);
                    }else throw new InvalidOrderStatusException("orderId: "+ id);
                    return mapper.toDto(order);
                });
    }

    @Override
    @Transactional
    public Optional<OrderDto> cancel(Long id) {
        return repository.findById(id)
                .map(order -> {
                    checkEditable(order);
                    OrderRequest request = repository.createCancelRequest(order);
                    update(id, request);
                    order.setOrderStatus(OrderStatus.CANCELLED);
                    return mapper.toDto(order);
                });
    }

    @Override
    @Transactional
    public Optional<OrderDto> confirm(Long id) {
        return repository.findById(id)
                .map(order -> {
                    checkEditable(order);
                    OrderRequest request = repository.createConfirmRequest(order);
                    update(id, request);
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                    return mapper.toDto(order);
                });
    }

    @Override
    public List<OrderDto> findByCustomer(Long customerId) {
        return mapper.toDtoList(repository.findByCustomer(customerId));
    }

    //helper methods
    private void loadCustomer(Long customerId){
        if (userRepository.findById(customerId).isEmpty()) {
            throw new CustomerNotFoundException("customer "+ customerId +"doesn't exist");
        }
    }

    private void buildItems(){}// ????

    private BigDecimal calculateTotal(Order order){
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()){
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private void checkEditable(Order order){
        if (order.getOrderStatus() != OrderStatus.NEW)
            throw new OrderNotEditableException(order.getId().toString());
    }
}