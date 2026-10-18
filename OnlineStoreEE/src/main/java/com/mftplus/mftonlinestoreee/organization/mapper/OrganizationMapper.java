package com.mftplus.mftonlinestoreee.organization.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationDto;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationRequest;
import com.mftplus.mftonlinestoreee.organization.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface OrganizationMapper extends BaseMapper<Organization, OrganizationDto, OrganizationRequest> {

    @Override
    @Mapping(target = "status", ignore = true)
    Organization toEntity(OrganizationRequest request);

    @Override
    @Mapping(target = "status", ignore = true)
    void updateEntity(OrganizationRequest request, @MappingTarget Organization entity);
}
