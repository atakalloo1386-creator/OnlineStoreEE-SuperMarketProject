package com.mftplus.mftonlinestoreee.orders.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.orders.dto.OrderItemDto;
import com.mftplus.mftonlinestoreee.orders.dto.OrderItemRequest;
import com.mftplus.mftonlinestoreee.orders.entity.OrderItem;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDto, OrderItemRequest> {

}
