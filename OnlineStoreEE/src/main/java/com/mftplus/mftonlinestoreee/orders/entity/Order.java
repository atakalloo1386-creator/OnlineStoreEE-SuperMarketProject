package com.mftplus.mftonlinestoreee.orders.entity;


import com.mftplus.mftonlinestoreee.core.entity.BaseEntity;

import com.mftplus.mftonlinestoreee.financials.entity.Payment;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderStatus;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderType;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingMethod;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingStatus;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "update orders set deleted=true, deleted_at=current_timestamp where id=? and version=?")
@SQLRestriction("deleted = false")

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingMethod shippingMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus shippingStatus;

    @OneToOne
    @JoinColumn(nullable = false)
    private Payment paymentId;


    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

}