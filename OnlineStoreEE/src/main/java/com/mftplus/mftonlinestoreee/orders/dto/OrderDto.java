package com.mftplus.mftonlinestoreee.orders.dto;

import com.mftplus.mftonlinestoreee.orders.entity.OrderItem;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderStatus;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderType;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingMethod;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingStatus;
import com.mftplus.mftonlinestoreee.users.entity.User;


import java.math.BigDecimal;
import java.util.List;

public record OrderDto (
        Long id,

        List<OrderItem> orderItems,

        User customer,

        ShippingMethod shippingMethod,

        ShippingStatus shippingStatus,

        BigDecimal total,

        OrderType orderType,

        OrderStatus orderStatus) {}