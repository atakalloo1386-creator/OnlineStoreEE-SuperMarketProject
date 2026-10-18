package com.mftplus.mftonlinestoreee.orders.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.orders.dto.OrderRequest;
import com.mftplus.mftonlinestoreee.orders.entity.Order;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderStatus;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingStatus;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class OrderRepository extends BaseRepository<Order> {
    @Inject
    UserRepository userRepository;

    public OrderRepository() {
    }

    public OrderRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Order> entityType() {
        return Order.class;
    }

    public List<Order> findByCustomer(Long customerId) {
        return entityManager
                .createQuery("select o from Order o"
                        + " where o.customer = :customer"
                        + " order by o.id", Order.class)
                .setParameter("customer", userRepository.findById(customerId))
                .getResultList();
    }

    public OrderRequest createConfirmRequest(Order order){
        return new OrderRequest(
                order.getOrderItems(),
                order.getCustomer(),
                order.getShippingMethod(),
                order.getShippingStatus(),
                order.getTotal(),
                order.getOrderType(),
                OrderStatus.CONFIRMED);
    }

    public OrderRequest createCancelRequest(Order order){
        return new OrderRequest(
                order.getOrderItems(),
                order.getCustomer(),
                order.getShippingMethod(),
                order.getShippingStatus(),
                order.getTotal(),
                order.getOrderType(),
                OrderStatus.CANCELLED);
    }

    public OrderRequest createShipRequest(Order order){
        return new OrderRequest(
                order.getOrderItems(),
                order.getCustomer(),
                order.getShippingMethod(),
                ShippingStatus.SHIPPED,
                order.getTotal(),
                order.getOrderType(),
                order.getOrderStatus());
    }

    public OrderRequest createDeliverRequest(Order order){
        return new OrderRequest(
                order.getOrderItems(),
                order.getCustomer(),
                order.getShippingMethod(),
                ShippingStatus.DELIVERED,
                order.getTotal(),
                order.getOrderType(),
                order.getOrderStatus());
    }
}
