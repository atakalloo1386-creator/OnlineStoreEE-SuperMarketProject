package com.mftplus.mftonlinestoreee.delivery.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryDto;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryRequest;

import java.util.List;

public interface DeliveryService extends BaseService<DeliveryRequest, DeliveryDto> {
    DeliveryDto send(Long id);

    DeliveryDto confirmDelivery(Long id);

    List<DeliveryDto> findByCourier(Long courierId);
}
