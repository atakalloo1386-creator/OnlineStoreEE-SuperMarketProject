package com.mftplus.mftonlinestoreee.inventories.dto;

import java.time.LocalDate;

public record InventoryItemDto(
        Long id,
        Long inventoryId,
        Long productId,
        String productName,
        Integer quantity,
        Integer minimumQuantity,
        String batchNumber,
        LocalDate expirationDate){
}