package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.Bank;
import com.mftplus.mftonlinestoreee.financials.entity.ChequePayment;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;
import com.mftplus.mftonlinestoreee.financials.exception.BankNotFoundException;
import com.mftplus.mftonlinestoreee.financials.exception.InvalidPaymentStatusException;
import com.mftplus.mftonlinestoreee.financials.exception.PaymentNotEditableException;
import com.mftplus.mftonlinestoreee.financials.mapper.ChequePaymentMapper;
import com.mftplus.mftonlinestoreee.financials.repository.BankRepository;
import com.mftplus.mftonlinestoreee.financials.repository.ChequePaymentRepository;
import com.mftplus.mftonlinestoreee.financials.service.ChequePaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class ChequePaymentServiceImpl implements ChequePaymentService {
    @Inject
    private ChequePaymentRepository repository;
    @Inject
    private BankRepository bankRepository;
    @Inject
    private ChequePaymentMapper mapper;


    @Transactional
    @Override
    public ChequePaymentDto create(ChequePaymentRequest request) {
        Bank bank = loadBank(request.bankId());

        ChequePayment payment = mapper.toEntity(request);
        payment.setType(PaymentType.CHEQUE);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setBank(bank);
        return mapper.toDto(repository.save(payment));
    }

    @Transactional
    @Override
    public Optional<ChequePaymentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<ChequePaymentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public PageResult<ChequePaymentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<ChequePaymentDto> update(Long id, ChequePaymentRequest request) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    Bank bank = loadBank(request.bankId());

                    mapper.updateEntity(request, payment);
                    payment.setBank(bank);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<ChequePaymentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    repository.delete(payment);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<ChequePaymentDto> clear(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkPending(payment);
                    payment.setStatus(PaymentStatus.COMPLETED);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<ChequePaymentDto> bounce(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkPending(payment);
                    payment.setStatus(PaymentStatus.FAILED);
                    return mapper.toDto(payment);
                });
    }


    private void checkEditable(ChequePayment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentNotEditableException(payment.getId(), payment.getStatus());
        }
    }


    private void checkPending(ChequePayment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(payment.getId(), payment.getStatus());
        }
    }

    private Bank loadBank(Long bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new BankNotFoundException(bankId));
    }
}
