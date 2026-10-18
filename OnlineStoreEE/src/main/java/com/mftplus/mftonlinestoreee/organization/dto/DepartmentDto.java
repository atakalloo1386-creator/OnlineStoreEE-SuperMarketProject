package com.mftplus.mftonlinestoreee.organization.dto;

import com.mftplus.mftonlinestoreee.organization.entity.enums.DepartmentStatus;

public record DepartmentDto(
        Long id,
        Long branchId,
        String branchName,
        String name,
        String code,
        String description,
        DepartmentStatus status) {
}
