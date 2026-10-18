package com.mftplus.mftonlinestoreee.delivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourierRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 15) String phone,
        @Email String email,
        @NotBlank @Size(max = 200) String vehicleType
) {
}
