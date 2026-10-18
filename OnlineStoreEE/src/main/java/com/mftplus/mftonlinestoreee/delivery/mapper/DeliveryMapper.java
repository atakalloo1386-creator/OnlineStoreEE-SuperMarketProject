package com.mftplus.mftonlinestoreee.delivery.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryDto;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryRequest;
import com.mftplus.mftonlinestoreee.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface DeliveryMapper extends BaseMapper<Delivery, DeliveryDto, DeliveryRequest> {
    @Mapping(source = "courier.id", target = "courierId")
    @Mapping(source = "courier.name", target = "courierName")
    DeliveryDto toDto(Delivery entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sendDate", ignore = true)
    @Mapping(target = "receiveDate", ignore = true)
    @Mapping(target = "courier", ignore = true)
    Delivery toEntity(DeliveryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sendDate", ignore = true)
    @Mapping(target = "receiveDate", ignore = true)
    @Mapping(target = "courier", ignore = true)
    void updateEntity(DeliveryRequest request, @MappingTarget Delivery entity);
}
