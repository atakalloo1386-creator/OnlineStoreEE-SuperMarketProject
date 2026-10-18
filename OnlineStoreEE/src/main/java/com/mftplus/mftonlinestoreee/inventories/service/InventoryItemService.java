package com.mftplus.mftonlinestoreee.inventories.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;

import java.util.List;

public interface InventoryItemService extends BaseService<InventoryItemRequest,InventoryItemDto> {

    List<InventoryItemDto> findByInventory(Long inventoryId);

    List<InventoryItemDto> findLowStock();
}