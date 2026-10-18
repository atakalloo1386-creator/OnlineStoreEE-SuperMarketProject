package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierDto;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Cashier;
import com.mftplus.mftonlinestoreee.financials.exception.DuplicateEmployeeCodeException;
import com.mftplus.mftonlinestoreee.financials.mapper.CashierMapper;
import com.mftplus.mftonlinestoreee.financials.repository.CashierRepository;
import com.mftplus.mftonlinestoreee.financials.service.CashierService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class CashierServiceImpl implements CashierService {
    @Inject
    private CashierRepository repository;
    @Inject
    private CashierMapper mapper;

    @Transactional
    @Override
    public CashierDto create(CashierRequest request) {
        checkEmployeeCodeIsFree(request, null);
        Cashier cashier = mapper.toEntity(request);
        return mapper.toDto(repository.save(cashier));
    }

    @Override
    public Optional<CashierDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<CashierDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<CashierDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<CashierDto> update(Long id, CashierRequest request) {
        return repository.findById(id)
                .map(cashier -> {
                    checkEmployeeCodeIsFree(request, id);
                    mapper.updateEntity(request, cashier);
                    return mapper.toDto(cashier);
                });
    }

    @Transactional
    @Override
    public Optional<CashierDto> deleteById(Long id) {
        return repository.findById(id)
                .map(cashier -> {
                    repository.delete(cashier);
                    return mapper.toDto(cashier);
                });
    }


    private void checkEmployeeCodeIsFree(CashierRequest request, Long currentId) {
        repository.findByEmployeeCode(request.employeeCode())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateEmployeeCodeException(request.employeeCode());
                });
    }
}
