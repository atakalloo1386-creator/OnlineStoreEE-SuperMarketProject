package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionDto;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionRequest;
import com.mftplus.mftonlinestoreee.financials.entity.BankAccount;
import com.mftplus.mftonlinestoreee.financials.entity.Cashier;
import com.mftplus.mftonlinestoreee.financials.entity.Transaction;
import com.mftplus.mftonlinestoreee.financials.entity.enums.TransactionType;
import com.mftplus.mftonlinestoreee.financials.exception.BankAccountNotFoundException;
import com.mftplus.mftonlinestoreee.financials.exception.CashierNotFoundException;
import com.mftplus.mftonlinestoreee.financials.exception.InsufficientBalanceException;
import com.mftplus.mftonlinestoreee.financials.mapper.TransactionMapper;
import com.mftplus.mftonlinestoreee.financials.repository.BankAccountRepository;
import com.mftplus.mftonlinestoreee.financials.repository.CashierRepository;
import com.mftplus.mftonlinestoreee.financials.repository.TransactionRepository;
import com.mftplus.mftonlinestoreee.financials.service.TransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
@Loggable
public class TransactionServiceImpl implements TransactionService {
    @Inject
    private TransactionRepository repository;
    @Inject
    private BankAccountRepository bankAccountRepository;
    @Inject
    private CashierRepository cashierRepository;
    @Inject
    private TransactionMapper mapper;


    @Transactional
    @Override
    public TransactionDto create(TransactionRequest request) {
        BankAccount account = loadAccountForUpdate(request.bankAccountId());
        return mapper.toDto(save(account, request));
    }


    @Transactional
    @Override
    public Optional<TransactionDto> register(Long bankAccountId, TransactionRequest request) {
        return bankAccountRepository.findByIdForUpdate(bankAccountId)
                .map(account -> mapper.toDto(save(account, request)));
    }

    @Transactional
    @Override
    public Optional<TransactionDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<TransactionDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public PageResult<TransactionDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<TransactionDto> findByBankAccount(Long bankAccountId) {
        return mapper.toDtoList(repository.findByBankAccount(bankAccountId));
    }


    @Transactional
    @Override
    public Optional<TransactionDto> update(Long id, TransactionRequest request) {
        return repository.findById(id)
                .map(transaction -> {
                    BankAccount account = loadAccountForUpdate(transaction.getBankAccount().getId());
                    Cashier cashier = loadCashier(request.cashierId());
//                    takeBack(account, transaction);
                    applyToBalance(account, request.type(), request.amount());

                    mapper.updateEntity(request, transaction);
                    transaction.setCashier(cashier);
                    return mapper.toDto(transaction);
                });
    }

    @Transactional
    @Override
    public Optional<TransactionDto> deleteById(Long id) {
        return repository.findById(id)
                .map(transaction -> {
                    BankAccount account = loadAccountForUpdate(transaction.getBankAccount().getId());
//                    takeBack(account, transaction);
                    repository.delete(transaction);
                    return mapper.toDto(transaction);
                });
    }

    private Transaction save(BankAccount account, TransactionRequest request) {
        Cashier cashier = loadCashier(request.cashierId());

        applyToBalance(account, request.type(), request.amount());

        Transaction transaction = mapper.toEntity(request);
        transaction.setBankAccount(account);
        transaction.setCashier(cashier);
        return repository.save(transaction);
    }

    private BankAccount loadAccountForUpdate(Long bankAccountId) {
        return bankAccountRepository.findByIdForUpdate(bankAccountId)
                .orElseThrow(() -> new BankAccountNotFoundException(bankAccountId));
    }


    private Cashier loadCashier(Long cashierId) {
        if (cashierId == null) {
            return null;
        }
        return cashierRepository.findById(cashierId)
                .orElseThrow(() -> new CashierNotFoundException(cashierId));
    }

    // DEPOSIT adds to the balance, WITHDRAW subtracts (never below zero).
    private void applyToBalance(BankAccount account, TransactionType type, BigDecimal amount) {
        BigDecimal balance = account.getBalance();
        switch (type) {
            case DEPOSIT -> account.setBalance(balance.add(amount));
            case WITHDRAW -> {
                if (balance.compareTo(amount) < 0) {
                    throw new InsufficientBalanceException(balance, amount);
                }
                account.setBalance(balance.subtract(amount));
            }
        }
    }


//    private void takeBack(BankAccount account, Transaction transaction) {
//        TransactionType opposite = transaction.getType() == TransactionType.DEPOSIT
//                ? TransactionType.WITHDRAW
//                : TransactionType.DEPOSIT;
//        applyToBalance(account, opposite, transaction.getAmount());
//    }
}
