package com.mftplus.mftonlinestoreee.orders.dto;


import com.mftplus.mftonlinestoreee.orders.entity.Order;
import com.mftplus.mftonlinestoreee.products.entity.Product;

import java.math.BigDecimal;

public record OrderItemRequest (
        Order order,

        Product product,

        Integer quantity,

        BigDecimal unitPrice){}