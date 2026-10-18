package com.mftplus.mftonlinestoreee.inventories.dto;

import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record InventoryTransactionRequest(
        @NotNull Integer quantity,
        @NotNull InventoryTransactionType type,
        @NotNull InventoryReferenceType referenceType,
        Long referenceId,
        @NotBlank @Size(max = 150)String reason){
}