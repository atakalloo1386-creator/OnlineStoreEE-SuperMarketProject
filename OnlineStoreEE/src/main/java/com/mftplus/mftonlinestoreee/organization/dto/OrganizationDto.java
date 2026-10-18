package com.mftplus.mftonlinestoreee.organization.dto;

import com.mftplus.mftonlinestoreee.organization.entity.enums.OrganizationStatus;

import java.time.LocalDate;

public record OrganizationDto(
        Long id,
        String name,
        String registrationNumber,
        String email,
        String website,
        LocalDate establishmentDate,
        OrganizationStatus status) {
}
