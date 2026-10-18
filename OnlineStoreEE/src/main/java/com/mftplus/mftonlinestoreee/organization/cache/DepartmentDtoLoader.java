package com.mftplus.mftonlinestoreee.organization.cache;

import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.mapper.DepartmentMapper;
import com.mftplus.mftonlinestoreee.organization.repository.DepartmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class DepartmentDtoLoader {
    @Inject
    private DepartmentRepository repository;
    @Inject
    private DepartmentMapper mapper;

    @Transactional
    public Optional<DepartmentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }
}
