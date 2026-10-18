package com.mftplus.mftonlinestoreee.organization.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentRequest;
import com.mftplus.mftonlinestoreee.organization.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface DepartmentService extends BaseService<DepartmentRequest, DepartmentDto> {

    List<DepartmentDto> findByBranch(Long branchId);

    List<DepartmentDto> findByEmployee(Long userId);

    Optional<DepartmentDto> activate(Long id);

    Optional<DepartmentDto> deactivate(Long id);

    Optional<List<EmployeeDto>> findEmployees(Long departmentId);

    Optional<DepartmentDto> addEmployee(Long departmentId, Long userId);

    Optional<DepartmentDto> removeEmployee(Long departmentId, Long userId);
}
