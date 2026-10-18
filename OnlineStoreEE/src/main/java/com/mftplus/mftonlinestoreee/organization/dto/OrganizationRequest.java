package com.mftplus.mftonlinestoreee.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record OrganizationRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 50) String registrationNumber,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 100) String website,
        @PastOrPresent LocalDate establishmentDate) {
}
