package com.mftplus.mftonlinestoreee.inventories.dto;

import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;

import java.time.LocalDateTime;

public record InventoryTransactionDto(
        Long id,
        Long itemId,
        Integer quantity,
        InventoryTransactionType type,
        InventoryReferenceType referenceType,
        Long referenceId,
        String reason,
        LocalDateTime createdAt){
}