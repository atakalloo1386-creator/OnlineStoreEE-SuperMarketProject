package com.mftplus.mftonlinestoreee.orders.dto;

import com.mftplus.mftonlinestoreee.orders.entity.OrderItem;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderStatus;
import com.mftplus.mftonlinestoreee.orders.entity.enums.OrderType;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingMethod;
import com.mftplus.mftonlinestoreee.orders.entity.enums.ShippingStatus;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(

        @Valid
        @NotEmpty
        List<OrderItem> orderItems,

        User customer,

        ShippingMethod shippingMethod,

        ShippingStatus shippingStatus,

        BigDecimal total,

        OrderType orderType,

        OrderStatus orderStatus) {}
