package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.cashpayment.CashPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.cashpayment.CashPaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.CashPayment;
import com.mftplus.mftonlinestoreee.financials.entity.Cashier;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;
import com.mftplus.mftonlinestoreee.financials.exception.CashierNotFoundException;
import com.mftplus.mftonlinestoreee.financials.exception.PaymentNotEditableException;
import com.mftplus.mftonlinestoreee.financials.mapper.CashPaymentMapper;
import com.mftplus.mftonlinestoreee.financials.repository.CashPaymentRepository;
import com.mftplus.mftonlinestoreee.financials.repository.CashierRepository;
import com.mftplus.mftonlinestoreee.financials.service.CashPaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class CashPaymentServiceImpl implements CashPaymentService {
    @Inject
    private CashPaymentRepository repository;
    @Inject
    private CashierRepository cashierRepository;
    @Inject
    private CashPaymentMapper mapper;


    @Transactional
    @Override
    public CashPaymentDto create(CashPaymentRequest request) {
        Cashier cashier = loadCashier(request.cashierId());

        CashPayment payment = mapper.toEntity(request);
        payment.setType(PaymentType.CASH);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCashier(cashier);
        return mapper.toDto(repository.save(payment));
    }

    @Transactional
    @Override
    public Optional<CashPaymentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<CashPaymentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public PageResult<CashPaymentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<CashPaymentDto> update(Long id, CashPaymentRequest request) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    Cashier cashier = loadCashier(request.cashierId());

                    mapper.updateEntity(request, payment);
                    payment.setCashier(cashier);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<CashPaymentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    repository.delete(payment);
                    return mapper.toDto(payment);
                });
    }


    private void checkEditable(CashPayment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentNotEditableException(payment.getId(), payment.getStatus());
        }
    }

    private Cashier loadCashier(Long cashierId) {
        return cashierRepository.findById(cashierId)
                .orElseThrow(() -> new CashierNotFoundException(cashierId));
    }
}
