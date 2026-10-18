package com.mftplus.mftonlinestoreee.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotNull @Positive Long branchId,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 50) String code,
        @Size(max = 500) String description) {
}
