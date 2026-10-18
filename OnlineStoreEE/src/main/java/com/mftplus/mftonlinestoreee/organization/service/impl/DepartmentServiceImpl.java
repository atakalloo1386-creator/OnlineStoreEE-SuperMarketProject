package com.mftplus.mftonlinestoreee.organization.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.cache.CacheInvalidation;
import com.mftplus.mftonlinestoreee.organization.cache.DepartmentDtoLoader;
import com.mftplus.mftonlinestoreee.organization.cache.RedisDtoCache;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentRequest;
import com.mftplus.mftonlinestoreee.organization.dto.EmployeeDto;
import com.mftplus.mftonlinestoreee.organization.entity.Branch;
import com.mftplus.mftonlinestoreee.organization.entity.Department;
import com.mftplus.mftonlinestoreee.organization.entity.enums.BranchStatus;
import com.mftplus.mftonlinestoreee.organization.exception.BranchNotFoundException;
import com.mftplus.mftonlinestoreee.organization.exception.DuplicateDepartmentCodeException;
import com.mftplus.mftonlinestoreee.organization.exception.EmployeeNotFoundException;
import com.mftplus.mftonlinestoreee.organization.exception.InactiveBranchException;
import com.mftplus.mftonlinestoreee.organization.mapper.DepartmentMapper;
import com.mftplus.mftonlinestoreee.organization.repository.BranchRepository;
import com.mftplus.mftonlinestoreee.organization.repository.DepartmentRepository;
import com.mftplus.mftonlinestoreee.organization.service.DepartmentService;
import com.mftplus.mftonlinestoreee.users.entity.User;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DepartmentServiceImpl implements DepartmentService {
    @Inject
    private DepartmentRepository repository;
    @Inject
    private BranchRepository branchRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private DepartmentMapper mapper;
    @Inject
    private DepartmentDtoLoader loader;
    @Inject
    private RedisDtoCache cache;
    @Inject
    private Event<CacheInvalidation> cacheInvalidations;

    @Transactional
    @Override
    public DepartmentDto create(DepartmentRequest request) {
        checkCodeIsFree(request, null);
        Branch branch = loadActiveBranch(request.branchId());
        Department department = mapper.toEntity(request);
        department.setBranch(branch);
        return mapper.toDto(repository.save(department));
    }

    @Override
    public Optional<DepartmentDto> findById(Long id) {
        if (cache.inTransaction()) {
            return loader.findById(id);
        }
        Optional<DepartmentDto> cached = cache.get(CacheInvalidation.Region.DEPARTMENT, id, DepartmentDto.class);
        if (cached.isPresent()) {
            return cached;
        }
        Optional<DepartmentDto> result = loader.findById(id);
        result.ifPresent(dto -> cache.put(CacheInvalidation.Region.DEPARTMENT, id, dto));
        return result;
    }

    @Transactional
    @Override
    public List<DepartmentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public PageResult<DepartmentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<DepartmentDto> findByBranch(Long branchId) {
        return mapper.toDtoList(repository.findByBranchId(branchId));
    }

    @Transactional
    @Override
    public List<DepartmentDto> findByEmployee(Long userId) {
        return mapper.toDtoList(repository.findByEmployeeId(userId));
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> update(Long id, DepartmentRequest request) {
        return repository.findById(id)
                .map(department -> {
                    checkCodeIsFree(request, id);
                    Branch branch = loadActiveBranch(request.branchId());
                    mapper.updateEntity(request, department);
                    department.setBranch(branch);
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.DEPARTMENT, id));
                    return mapper.toDto(department);
                });
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(department -> {
                    DepartmentDto dto = mapper.toDto(department);
                    repository.delete(department);
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.DEPARTMENT, id));
                    return dto;
                });
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> activate(Long id) {
        return repository.findById(id)
                .map(department -> {
                    department.activate();
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.DEPARTMENT, id));
                    return mapper.toDto(department);
                });
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> deactivate(Long id) {
        return repository.findById(id)
                .map(department -> {
                    department.deactivate();
                    cacheInvalidations.fire(new CacheInvalidation(CacheInvalidation.Region.DEPARTMENT, id));
                    return mapper.toDto(department);
                });
    }

    @Transactional
    @Override
    public Optional<List<EmployeeDto>> findEmployees(Long departmentId) {
        return repository.findById(departmentId)
                .map(department -> mapper.toEmployeeDtoList(department.getEmployees()));
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> addEmployee(Long departmentId, Long userId) {
        return repository.findById(departmentId)
                .map(department -> {
                    User employee = loadEmployee(userId);
                    if (!isEmployee(department, userId)) {
                        department.getEmployees().add(employee);
                    }
                    return mapper.toDto(department);
                });
    }

    @Transactional
    @Override
    public Optional<DepartmentDto> removeEmployee(Long departmentId, Long userId) {
        return repository.findById(departmentId)
                .map(department -> {
                    department.getEmployees().removeIf(user -> user.getId().equals(userId));
                    return mapper.toDto(department);
                });
    }

    private void checkCodeIsFree(DepartmentRequest request, Long currentId) {
        repository.findByBranchIdAndCode(request.branchId(), request.code())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateDepartmentCodeException(request.code());
                });
    }

    private Branch loadActiveBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));
        if (branch.getStatus() != BranchStatus.ACTIVE) {
            throw new InactiveBranchException(branchId);
        }
        return branch;
    }

    private User loadEmployee(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EmployeeNotFoundException(userId));
    }

    private boolean isEmployee(Department department, Long userId) {
        return department.getEmployees().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}
