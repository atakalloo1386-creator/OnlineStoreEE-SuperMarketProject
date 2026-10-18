package com.mftplus.mftonlinestoreee.delivery.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierDto;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierRequest;
import com.mftplus.mftonlinestoreee.delivery.entity.Courier;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CourierMapper extends BaseMapper<Courier, CourierDto, CourierRequest> {
}
