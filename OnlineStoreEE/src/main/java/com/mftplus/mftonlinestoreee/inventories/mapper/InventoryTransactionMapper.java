package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface InventoryTransactionMapper extends
        BaseMapper<InventoryTransaction,
                InventoryTransactionDto,
                InventoryTransactionRequest> {

    @Mapping(source = "inventoryItem.id", target = "itemId")
    @Mapping(source = "transactionType", target = "type")
    InventoryTransactionDto toDto(InventoryTransaction entity);

    @Mapping(target = "inventoryItem", ignore = true)
    @Mapping(source = "type", target = "transactionType")
    InventoryTransaction toEntity(InventoryTransactionRequest request);

    @Mapping(target = "inventoryItem", ignore = true)
    @Mapping(source = "type", target = "transactionType")
    void updateEntity(
            InventoryTransactionRequest request,
            @MappingTarget InventoryTransaction entity
    );
}