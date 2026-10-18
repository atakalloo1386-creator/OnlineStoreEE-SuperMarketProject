package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(config = MapStructConfig.class)
public interface InventoryItemMapper extends BaseMapper<InventoryItem, InventoryItemDto,InventoryItemRequest > {

    @Mapping(source = "inventory.id", target = "inventoryId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    InventoryItemDto toDto(InventoryItem entity);

    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    InventoryItem toEntity(InventoryItemRequest request);

    void updateEntity(InventoryItemRequest request, @MappingTarget InventoryItem entity);
}