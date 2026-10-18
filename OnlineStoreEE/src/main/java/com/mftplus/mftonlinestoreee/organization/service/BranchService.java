package com.mftplus.mftonlinestoreee.organization.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.organization.dto.BranchDto;
import com.mftplus.mftonlinestoreee.organization.dto.BranchRequest;

import java.util.List;
import java.util.Optional;

public interface BranchService extends BaseService<BranchRequest, BranchDto> {

    List<BranchDto> findByOrganization(Long organizationId);

    Optional<BranchDto> activate(Long id);

    Optional<BranchDto> deactivate(Long id);
}
