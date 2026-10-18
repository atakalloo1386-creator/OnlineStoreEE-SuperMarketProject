package com.mftplus.mftonlinestoreee.organization.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.cache.CacheInvalidation;
import com.mftplus.mftonlinestoreee.organization.dto.BranchDto;
import com.mftplus.mftonlinestoreee.organization.dto.BranchRequest;
import com.mftplus.mftonlinestoreee.organization.entity.Branch;
import com.mftplus.mftonlinestoreee.organization.entity.Organization;
import com.mftplus.mftonlinestoreee.organization.entity.enums.OrganizationStatus;
import com.mftplus.mftonlinestoreee.organization.exception.BranchHasDepartmentsException;
import com.mftplus.mftonlinestoreee.organization.exception.DuplicateBranchCodeException;
import com.mftplus.mftonlinestoreee.organization.exception.InactiveOrganizationException;
import com.mftplus.mftonlinestoreee.organization.exception.OrganizationNotFoundException;
import com.mftplus.mftonlinestoreee.organization.mapper.BranchMapper;
import com.mftplus.mftonlinestoreee.organization.repository.BranchRepository;
import com.mftplus.mftonlinestoreee.organization.repository.DepartmentRepository;
import com.mftplus.mftonlinestoreee.organization.repository.OrganizationRepository;
import com.mftplus.mftonlinestoreee.organization.service.BranchService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

// BranchDto reads the lazy organization, so read methods run in a transaction too.
@ApplicationScoped
public class BranchServiceImpl implements BranchService {
    @Inject
    private BranchRepository repository;
    @Inject
    private OrganizationRepository organizationRepository;
    @Inject
    private DepartmentRepository departmentRepository;
    @Inject
    private BranchMapper mapper;
    @Inject
    private Event<CacheInvalidation> cacheInvalidations;

    @Transactional
    @Override
    public BranchDto create(BranchRequest request) {
        checkBranchCodeIsFree(request, null);
        Organization organization = loadActiveOrganization(request.organizationId());
        Branch branch = mapper.toEntity(request);
        branch.setOrganization(organization);
        return mapper.toDto(repository.save(branch));
    }

    @Transactional
    @Override
    public Optional<BranchDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<BranchDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public PageResult<BranchDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<BranchDto> findByOrganization(Long organizationId) {
        return mapper.toDtoList(repository.findByOrganizationId(organizationId));
    }

    @Transactional
    @Override
    public Optional<BranchDto> update(Long id, BranchRequest request) {
        return repository.findById(id)
                .map(branch -> {
                    checkBranchCodeIsFree(request, id);
                    Organization organization = loadActiveOrganization(request.organizationId());
                    mapper.updateEntity(request, branch);
                    branch.setOrganization(organization);
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.ALL_DEPARTMENTS, null));
                    return mapper.toDto(branch);
                });
    }

    @Transactional
    @Override
    public Optional<BranchDto> deleteById(Long id) {
        return repository.findById(id)
                .map(branch -> {
                    checkHasNoDepartments(id);
                    BranchDto dto = mapper.toDto(branch);
                    repository.delete(branch);
                    return dto;
                });
    }

    @Transactional
    @Override
    public Optional<BranchDto> activate(Long id) {
        return repository.findById(id)
                .map(branch -> {
                    branch.activate();
                    return mapper.toDto(branch);
                });
    }

    @Transactional
    @Override
    public Optional<BranchDto> deactivate(Long id) {
        return repository.findById(id)
                .map(branch -> {
                    branch.deactivate();
                    return mapper.toDto(branch);
                });
    }

    private void checkBranchCodeIsFree(BranchRequest request, Long currentId) {
        repository.findByBranchCode(request.branchCode())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateBranchCodeException(request.branchCode());
                });
    }

    private Organization loadActiveOrganization(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
        if (organization.getStatus() != OrganizationStatus.ACTIVE) {
            throw new InactiveOrganizationException(organizationId);
        }
        return organization;
    }

    private void checkHasNoDepartments(Long id) {
        if (departmentRepository.countByBranchId(id) > 0) {
            throw new BranchHasDepartmentsException(id);
        }
    }
}
