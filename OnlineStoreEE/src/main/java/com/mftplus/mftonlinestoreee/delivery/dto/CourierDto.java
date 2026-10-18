package com.mftplus.mftonlinestoreee.delivery.dto;

public record CourierDto(
        Long id,
        String name,
        String phone,
        String email,
        String vehicleType
) {
}
