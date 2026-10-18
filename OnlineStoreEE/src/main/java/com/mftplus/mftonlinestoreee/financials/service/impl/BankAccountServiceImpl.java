package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountDto;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Bank;
import com.mftplus.mftonlinestoreee.financials.entity.BankAccount;
import com.mftplus.mftonlinestoreee.financials.exception.BankNotFoundException;
import com.mftplus.mftonlinestoreee.financials.exception.DuplicateAccountNumberException;
import com.mftplus.mftonlinestoreee.financials.mapper.BankAccountMapper;
import com.mftplus.mftonlinestoreee.financials.repository.BankAccountRepository;
import com.mftplus.mftonlinestoreee.financials.repository.BankRepository;
import com.mftplus.mftonlinestoreee.financials.service.BankAccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
@Loggable
public class BankAccountServiceImpl implements BankAccountService {
    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private BankRepository bankRepository;

    @Inject
    private BankAccountMapper bankAccountMapper;

    @Transactional
    @Override
    public BankAccountDto create(BankAccountRequest request) {
        checkAccountNumberIsFree(request, null);
        Bank bank = loadBank(request.bankId());
        BankAccount account = bankAccountMapper.toEntity(request);
        account.setBank(bank);
        account.setBalance(BigDecimal.ZERO);
        return bankAccountMapper.toDto(bankAccountRepository.save(account));
    }

    @Transactional
    @Override
    public Optional<BankAccountDto> findById(Long id) {
        return bankAccountRepository.findById(id).map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public List<BankAccountDto> findAll() {
        return bankAccountMapper.toDtoList(bankAccountRepository.findAll());
    }

    @Transactional
    @Override
    public PageResult<BankAccountDto> findAll(int page, int size) {
        return bankAccountRepository.findPage(page, size).map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public List<BankAccountDto> findByBank(Long bankId) {
        return bankAccountMapper.toDtoList(bankAccountRepository.findByBank(bankId));
    }

    @Transactional
    @Override
    public Optional<BankAccountDto> update(Long id, BankAccountRequest request) {
        return bankAccountRepository.findById(id)
                .map(account -> {
                    checkAccountNumberIsFree(request, id);
                    Bank bank = loadBank(request.bankId());
                    bankAccountMapper.updateEntity(request, account);
                    account.setBank(bank);
                    return bankAccountMapper.toDto(account);
                });
    }

    @Transactional
    @Override
    public Optional<BankAccountDto> deleteById(Long id) {
        return bankAccountRepository.findById(id)
                .map(account -> {
                    bankAccountRepository.delete(account);
                    return bankAccountMapper.toDto(account);
                });
    }


    private void checkAccountNumberIsFree(BankAccountRequest request, Long currentId) {
        bankAccountRepository.findByAccountNumber(request.accountNumber())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateAccountNumberException(request.accountNumber());
                });
    }

    private Bank loadBank(Long bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new BankNotFoundException(bankId));
    }
}
