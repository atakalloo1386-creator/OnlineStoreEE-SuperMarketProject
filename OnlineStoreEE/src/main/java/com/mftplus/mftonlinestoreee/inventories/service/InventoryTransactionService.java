package com.mftplus.mftonlinestoreee.inventories.service;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;

import java.util.List;
import java.util.Optional;

public interface InventoryTransactionService{
    List<InventoryTransactionDto> findByItem(Long itemId);

    Optional<InventoryTransactionDto> register(Long itemId, InventoryTransactionRequest request);
}