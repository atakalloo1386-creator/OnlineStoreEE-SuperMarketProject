package com.mftplus.mftonlinestoreee.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BranchRequest(
        @NotNull @Positive Long organizationId,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 50) String branchCode,
        @Size(max = 20) String phoneNumber,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 100) String website,
        LocalDate openingDate,
        @NotBlank @Size(max = 150) String address) {
}
