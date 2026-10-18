package com.mftplus.mftonlinestoreee.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record DeliveryRequest(
        @NotBlank @Size(max = 300, min = 30) String address,
        @NotBlank @Size(max = 60) String trackingNumber,
        @Size(max = 500) String deliveryNote,
        @NotNull Long courierId
) {
}
