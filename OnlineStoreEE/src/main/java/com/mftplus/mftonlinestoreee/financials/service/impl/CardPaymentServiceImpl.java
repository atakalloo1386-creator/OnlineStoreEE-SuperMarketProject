package com.mftplus.mftonlinestoreee.financials.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.financials.dto.cardpayment.CardPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.cardpayment.CardPaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.CardPayment;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;
import com.mftplus.mftonlinestoreee.financials.exception.PaymentNotEditableException;
import com.mftplus.mftonlinestoreee.financials.mapper.CardPaymentMapper;
import com.mftplus.mftonlinestoreee.financials.repository.CardPaymentRepository;
import com.mftplus.mftonlinestoreee.financials.service.CardPaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class CardPaymentServiceImpl implements CardPaymentService {
    @Inject
    private CardPaymentRepository repository;
    @Inject
    private CardPaymentMapper mapper;


    @Transactional
    @Override
    public CardPaymentDto create(CardPaymentRequest request) {
        CardPayment payment = mapper.toEntity(request);
        payment.setType(PaymentType.CARD);
        payment.setStatus(PaymentStatus.COMPLETED);
        return mapper.toDto(repository.save(payment));
    }

    @Override
    public Optional<CardPaymentDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<CardPaymentDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<CardPaymentDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<CardPaymentDto> update(Long id, CardPaymentRequest request) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    mapper.updateEntity(request, payment);
                    return mapper.toDto(payment);
                });
    }

    @Transactional
    @Override
    public Optional<CardPaymentDto> deleteById(Long id) {
        return repository.findById(id)
                .map(payment -> {
                    checkEditable(payment);
                    repository.delete(payment);
                    return mapper.toDto(payment);
                });
    }


    private void checkEditable(CardPayment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentNotEditableException(payment.getId(), payment.getStatus());
        }
    }
}
