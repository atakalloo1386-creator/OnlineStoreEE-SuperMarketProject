package com.mftplus.mftonlinestoreee.organization.dto;

import com.mftplus.mftonlinestoreee.organization.entity.enums.BranchStatus;

import java.time.LocalDate;

public record BranchDto(
        Long id,
        Long organizationId,
        String organizationName,
        String name,
        String branchCode,
        String phoneNumber,
        String email,
        String website,
        LocalDate openingDate,
        BranchStatus status,
        String address) {
}
