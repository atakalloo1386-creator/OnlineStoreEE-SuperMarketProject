package com.mftplus.mftonlinestoreee.organization.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentRequest;
import com.mftplus.mftonlinestoreee.organization.dto.EmployeeDto;
import com.mftplus.mftonlinestoreee.organization.entity.Department;
import com.mftplus.mftonlinestoreee.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DepartmentMapper extends BaseMapper<Department, DepartmentDto, DepartmentRequest> {

    @Override
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    DepartmentDto toDto(Department entity);

    @Override
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "status", ignore = true)
    Department toEntity(DepartmentRequest request);

    @Override
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(DepartmentRequest request, @MappingTarget Department entity);

    EmployeeDto toEmployeeDto(User user);

    List<EmployeeDto> toEmployeeDtoList(List<User> users);
}
