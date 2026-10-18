package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface InventoryMapper extends BaseMapper<Inventory, InventoryDto, InventoryRequest> {
}