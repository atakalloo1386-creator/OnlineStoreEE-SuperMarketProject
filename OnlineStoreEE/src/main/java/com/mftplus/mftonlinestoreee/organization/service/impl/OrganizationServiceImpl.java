package com.mftplus.mftonlinestoreee.organization.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.cache.CacheInvalidation;
import com.mftplus.mftonlinestoreee.organization.cache.RedisDtoCache;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationDto;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationRequest;
import com.mftplus.mftonlinestoreee.organization.entity.Organization;
import com.mftplus.mftonlinestoreee.organization.exception.DuplicateRegistrationNumberException;
import com.mftplus.mftonlinestoreee.organization.exception.OrganizationHasBranchesException;
import com.mftplus.mftonlinestoreee.organization.mapper.OrganizationMapper;
import com.mftplus.mftonlinestoreee.organization.repository.BranchRepository;
import com.mftplus.mftonlinestoreee.organization.repository.OrganizationRepository;
import com.mftplus.mftonlinestoreee.organization.service.OrganizationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrganizationServiceImpl implements OrganizationService {
    @Inject
    private OrganizationRepository repository;
    @Inject
    private BranchRepository branchRepository;
    @Inject
    private OrganizationMapper mapper;
    @Inject
    private RedisDtoCache cache;
    @Inject
    private Event<CacheInvalidation> cacheInvalidations;

    @Transactional
    @Override
    public OrganizationDto create(OrganizationRequest request) {
        checkRegistrationNumberIsFree(request, null);
        Organization organization = mapper.toEntity(request);
        return mapper.toDto(repository.save(organization));
    }

    @Override
    public Optional<OrganizationDto> findById(Long id) {
        if (cache.inTransaction()) {
            return repository.findById(id).map(mapper::toDto);
        }
        Optional<OrganizationDto> cached = cache.get(CacheInvalidation.Region.ORGANIZATION, id, OrganizationDto.class);
        if (cached.isPresent()) {
            return cached;
        }
        Optional<OrganizationDto> result = repository.findById(id).map(mapper::toDto);
        result.ifPresent(dto -> cache.put(CacheInvalidation.Region.ORGANIZATION, id, dto));
        return result;
    }

    @Override
    public List<OrganizationDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<OrganizationDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<OrganizationDto> update(Long id, OrganizationRequest request) {
        return repository.findById(id)
                .map(organization -> {
                    checkRegistrationNumberIsFree(request, id);
                    mapper.updateEntity(request, organization);
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.ORGANIZATION, id));
                    return mapper.toDto(organization);
                });
    }

    @Transactional
    @Override
    public Optional<OrganizationDto> deleteById(Long id) {
        return repository.findById(id)
                .map(organization -> {
                    checkHasNoBranches(id);
                    repository.delete(organization);
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.ORGANIZATION, id));
                    return mapper.toDto(organization);
                });
    }

    @Transactional
    @Override
    public Optional<OrganizationDto> activate(Long id) {
        return repository.findById(id)
                .map(organization -> {
                    organization.activate();
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.ORGANIZATION, id));
                    return mapper.toDto(organization);
                });
    }

    @Transactional
    @Override
    public Optional<OrganizationDto> deactivate(Long id) {
        return repository.findById(id)
                .map(organization -> {
                    organization.deactivate();
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.ORGANIZATION, id));
                    return mapper.toDto(organization);
                });
    }

    private void checkRegistrationNumberIsFree(OrganizationRequest request, Long currentId) {
        repository.findByRegistrationNumber(request.registrationNumber())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateRegistrationNumberException(request.registrationNumber());
                });
    }

    private void checkHasNoBranches(Long id) {
        if (branchRepository.countByOrganizationId(id) > 0) {
            throw new OrganizationHasBranchesException(id);
        }
    }
}
