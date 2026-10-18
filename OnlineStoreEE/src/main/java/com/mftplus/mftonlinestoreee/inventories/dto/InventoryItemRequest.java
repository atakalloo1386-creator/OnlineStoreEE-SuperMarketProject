package com.mftplus.mftonlinestoreee.inventories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InventoryItemRequest(
        @NotNull Long inventoryId,
        @NotNull Long productId,
        @NotNull Integer minimumQuantity,
        @NotBlank @Size(max = 60) String batchNumber,
        LocalDate expirationDate){
}