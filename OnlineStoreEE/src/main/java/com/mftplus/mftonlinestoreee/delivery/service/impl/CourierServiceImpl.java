package com.mftplus.mftonlinestoreee.delivery.service.impl;


import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierDto;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierRequest;
import com.mftplus.mftonlinestoreee.delivery.entity.Courier;
import com.mftplus.mftonlinestoreee.delivery.exception.DuplicateCourierEmailException;
import com.mftplus.mftonlinestoreee.delivery.mapper.CourierMapper;
import com.mftplus.mftonlinestoreee.delivery.repository.CourierRepository;
import com.mftplus.mftonlinestoreee.delivery.repository.DeliveryRepository;
import com.mftplus.mftonlinestoreee.delivery.service.CourierService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CourierServiceImpl implements CourierService {
    @Inject
    private CourierRepository courierRepository;
    @Inject
    private DeliveryRepository deliveryRepository;
    @Inject
    private CourierMapper courierMapper;

    @Transactional
    @Override
    public CourierDto create(CourierRequest request) {
        checkEmailIsFree(request, null);
        Courier courier = courierMapper.toEntity(request);
        return courierMapper.toDto(courierRepository.save(courier));
    }

    @Override
    public Optional<CourierDto> findById(Long id) {
        return courierRepository.findById(id).map(courierMapper::toDto);
    }

    @Override
    public List<CourierDto> findAll() {
        return courierMapper.toDtoList(courierRepository.findAll());
    }

    @Override
    public PageResult<CourierDto> findAll(int page, int size) {
        return courierRepository.findPage(page, size).map(courierMapper::toDto);
    }

    @Transactional
    @Override
    public Optional<CourierDto> update(Long id, CourierRequest request) {
        return courierRepository.findById(id)
                .map(courier -> {
                    checkEmailIsFree(request, id);
                    courierMapper.updateEntity(request, courier);
                    return courierMapper.toDto(courierRepository.update(courier));
                });
    }

    @Transactional
    @Override
    public Optional<CourierDto> deleteById(Long id) {
        return courierRepository.findById(id)
                .map(courier -> {
                    courierRepository.delete(courier);
                    return courierMapper.toDto(courier);
                });
    }

    private void checkEmailIsFree(CourierRequest request, Long currentId) {
        courierRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateCourierEmailException(request.email());
                });
    }
}
