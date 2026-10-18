package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentDto;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Payment;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.exception.PaymentNotEditableException;
import com.mftplus.mftonlinestoreee.financials.mapper.PaymentMapper;
import com.mftplus.mftonlinestoreee.financials.repository.PaymentRepository;
import com.mftplus.mftonlinestoreee.financials.service.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class PaymentServiceImpl implements PaymentService {
    @Inject
    private PaymentRepository repository;
    @Inject
    private PaymentMapper mapper;


    @Transactional
    @Override
    public PaymentDto create(PaymentRequest request) {
        Payment payment = mapper.toEntity(request);
        payment.setStatus(PaymentStatus.PENDING);
        return mapper.toDto(repository.save(payment));
    }

    @Override
    public Optional<PaymentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<PaymentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<PaymentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Override
    public List<PaymentDto> findByOrder(Long orderId) {
        return mapper.toDtoList(repository.findByOrder(orderId));
    }

    @Transactional
    @Override
    public Optional<PaymentDto> update(Long id, PaymentRequest request) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    mapper.updateEntity(request, payment);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<PaymentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    repository.delete(payment);
                    return mapper.toDto(payment);
                });
    }


    private void checkEditable(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentNotEditableException(payment.getId(), payment.getStatus());
        }
    }
}
