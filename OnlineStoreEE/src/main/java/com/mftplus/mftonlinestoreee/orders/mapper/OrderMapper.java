package com.mftplus.mftonlinestoreee.orders.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.orders.dto.OrderDto;
import com.mftplus.mftonlinestoreee.orders.dto.OrderRequest;
import com.mftplus.mftonlinestoreee.orders.entity.Order;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface OrderMapper extends BaseMapper<Order, OrderDto, OrderRequest> {

}
