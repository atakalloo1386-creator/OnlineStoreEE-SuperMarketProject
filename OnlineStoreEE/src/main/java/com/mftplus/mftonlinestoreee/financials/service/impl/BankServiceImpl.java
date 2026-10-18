package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankDto;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Bank;
import com.mftplus.mftonlinestoreee.financials.exception.BankInUseException;
import com.mftplus.mftonlinestoreee.financials.exception.DuplicateBankCodeException;
import com.mftplus.mftonlinestoreee.financials.mapper.BankMapper;
import com.mftplus.mftonlinestoreee.financials.repository.BankAccountRepository;
import com.mftplus.mftonlinestoreee.financials.repository.BankRepository;
import com.mftplus.mftonlinestoreee.financials.service.BankService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class BankServiceImpl implements BankService {
    @Inject
    private BankRepository repository;
    @Inject
    private BankAccountRepository bankAccountRepository;
    @Inject
    private BankMapper bankMapper;

    @Transactional
    @Override
    public BankDto create(BankRequest request) {
        checkCodeIsFree(request, null);
        Bank bank = bankMapper.toEntity(request);
        return bankMapper.toDto(repository.save(bank));
    }

    @Override
    public Optional<BankDto> findById(Long id) {
        return repository.findById(id).map(bankMapper::toDto);
    }

    @Override
    public List<BankDto> findAll() {
        return bankMapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<BankDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(bankMapper::toDto);
    }

    @Transactional
    @Override
    public Optional<BankDto> update(Long id, BankRequest request) {
        return repository.findById(id)
                .map(bank -> {
                    checkCodeIsFree(request, id);
                    bankMapper.updateEntity(request, bank);
                    return bankMapper.toDto(bank);
                });
    }

    @Transactional
    @Override
    public Optional<BankDto> deleteById(Long id) {
        return repository.findById(id)
                .map(bank -> {
                    checkNotInUse(id);
                    repository.delete(bank);
                    return bankMapper.toDto(bank);
                });
    }


    private void checkCodeIsFree(BankRequest request, Long currentId) {
        repository.findByCode(request.bankCode())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateBankCodeException(request.bankCode());
                });
    }


    private void checkNotInUse(Long bankId) {
        if (bankAccountRepository.countByBank(bankId) > 0) {
            throw new BankInUseException(bankId);
        }
    }
}
