package com.mftplus.mftonlinestoreee.delivery.dto;

import com.mftplus.mftonlinestoreee.delivery.entity.DeliveryStatus;

import java.time.LocalDateTime;

public record DeliveryDto(
        Long id,
        String courierName,
        String address,
        DeliveryStatus status,
        String trackingNumber,
        LocalDateTime sendDate,
        LocalDateTime receiveDate,
        String deliveryNote,
        Long courierId
) {
}
