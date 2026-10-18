package com.mftplus.mftonlinestoreee.organization.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.organization.dto.BranchDto;
import com.mftplus.mftonlinestoreee.organization.dto.BranchRequest;
import com.mftplus.mftonlinestoreee.organization.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface BranchMapper extends BaseMapper<Branch, BranchDto, BranchRequest> {

    @Override
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    BranchDto toDto(Branch entity);

    @Override
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "status", ignore = true)
    Branch toEntity(BranchRequest request);

    @Override
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(BranchRequest request, @MappingTarget Branch entity);
}
