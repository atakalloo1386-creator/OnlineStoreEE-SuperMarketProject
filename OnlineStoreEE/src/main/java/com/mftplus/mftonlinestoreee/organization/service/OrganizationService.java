package com.mftplus.mftonlinestoreee.organization.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationDto;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationRequest;

import java.util.Optional;

public interface OrganizationService extends BaseService<OrganizationRequest, OrganizationDto> {

    Optional<OrganizationDto> activate(Long id);

    Optional<OrganizationDto> deactivate(Long id);
}
