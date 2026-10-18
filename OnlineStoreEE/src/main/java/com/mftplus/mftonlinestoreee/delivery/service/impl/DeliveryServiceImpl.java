package com.mftplus.mftonlinestoreee.delivery.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryDto;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryRequest;
import com.mftplus.mftonlinestoreee.delivery.entity.Courier;
import com.mftplus.mftonlinestoreee.delivery.entity.Delivery;
import com.mftplus.mftonlinestoreee.delivery.exception.CourierNotFoundException;
import com.mftplus.mftonlinestoreee.delivery.exception.DuplicateTrackingNumberException;
import com.mftplus.mftonlinestoreee.delivery.mapper.DeliveryMapper;
import com.mftplus.mftonlinestoreee.delivery.repository.CourierRepository;
import com.mftplus.mftonlinestoreee.delivery.repository.DeliveryRepository;
import com.mftplus.mftonlinestoreee.delivery.service.DeliveryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DeliveryServiceImpl implements DeliveryService {
    @Inject
    private DeliveryRepository deliveryRepository;
    @Inject
    private DeliveryMapper deliveryMapper;
    @Inject
    private CourierRepository courierRepository;

    @Transactional
    @Override
    public DeliveryDto send(Long id) {
        Delivery delivery = getDelivery(id);
        delivery.send();
        return deliveryMapper.toDto(deliveryRepository.update(delivery));
    }

    @Transactional
    @Override
    public DeliveryDto confirmDelivery(Long id) {
        Delivery delivery = getDelivery(id);
        delivery.confirmDelivery();
        return deliveryMapper.toDto(deliveryRepository.update(delivery));
    }

    @Override
    @Transactional
    public List<DeliveryDto> findByCourier(Long courierId) {
        return deliveryMapper.toDtoList(deliveryRepository.findByCourier(courierId));
    }


//    @Override
//    public List<DeliveryDto> findByCourier(Long courierId) {
//        Delivery delivery = getDelivery(courierId);
//        delivery.confirmDelivery();
//        return deliveryMapper.toDto(deliveryRepository.update(delivery));
//    }

    @Transactional
    @Override
    public DeliveryDto create(DeliveryRequest request) {
        checkTrackingNumberIsFree(request, null);
        Delivery delivery = deliveryMapper.toEntity(request);
        delivery.setCourier(loadCourier(request.courierId()));
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public Optional<DeliveryDto> findById(Long id) {
        return deliveryRepository.findById(id).map(deliveryMapper::toDto);
    }

    @Override
    @Transactional
    public List<DeliveryDto> findAll() {
        return deliveryMapper.toDtoList(deliveryRepository.findAll());
    }

    @Override
    @Transactional
    public PageResult<DeliveryDto> findAll(int page, int size) {
        return deliveryRepository.findPage(page, size).map(deliveryMapper::toDto);
    }

    @Transactional
    @Override
    public Optional<DeliveryDto> update(Long id, DeliveryRequest request) {
        return deliveryRepository.findById(id)
                .map(delivery -> {
                    checkTrackingNumberIsFree(request, id);
                    deliveryMapper.updateEntity(request, delivery);
                    delivery.setCourier(loadCourier(request.courierId()));
                    return deliveryMapper.toDto(deliveryRepository.update(delivery));
                });
    }

    @Transactional
    @Override
    public Optional<DeliveryDto> deleteById(Long id) {
        return deliveryRepository.findById(id)
                .map(delivery -> {
                    deliveryRepository.delete(delivery);
                    return deliveryMapper.toDto(delivery);
                });
    }

    private Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Delivery not found" + id));
    }

    private Courier loadCourier(Long courierId) {
        return courierRepository.findById(courierId).orElseThrow(() -> new CourierNotFoundException(courierId));
    }

    private void checkTrackingNumberIsFree(DeliveryRequest request, Long currentId) {
        deliveryRepository.findByTrackingNumber(request.trackingNumber())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateTrackingNumberException(request.trackingNumber());
                });
    }
}
