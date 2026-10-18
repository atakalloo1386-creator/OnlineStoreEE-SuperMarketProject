package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTransactionMapperTest {

    private final InventoryTransactionMapper mapper =
            Mappers.getMapper(InventoryTransactionMapper.class);

    @Test
    void toEntity() {
        InventoryTransactionRequest request =
                new InventoryTransactionRequest(
                        10,
                        InventoryTransactionType.IN,
                        InventoryReferenceType.MANUAL,
                        null,
                        "Initial stock"
                );

        InventoryTransaction transaction = mapper.toEntity(request);

        assertNull(transaction.getId());
        assertNull(transaction.getInventoryItem());

        assertEquals(10, transaction.getQuantity());
        assertEquals(
                InventoryTransactionType.IN,
                transaction.getTransactionType()
        );
        assertEquals(
                InventoryReferenceType.MANUAL,
                transaction.getReferenceType()
        );
        assertNull(transaction.getReferenceId());
        assertEquals("Initial stock", transaction.getReason());
    }

    @Test
    void toDto() {
        LocalDateTime createdAt =
                LocalDateTime.of(2026, 10, 11, 10, 30);

        InventoryItem item = InventoryItem.builder()
                .id(5L)
                .quantity(20)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .build();

        InventoryTransaction transaction =
                InventoryTransaction.builder()
                        .id(1L)
                        .inventoryItem(item)
                        .quantity(10)
                        .transactionType(InventoryTransactionType.OUT)
                        .referenceType(InventoryReferenceType.ORDER)
                        .referenceId(100L)
                        .reason("Order dispatch")
                        .createdAt(createdAt)
                        .build();

        InventoryTransactionDto dto = mapper.toDto(transaction);

        assertEquals(1L, dto.id());
        assertEquals(5L, dto.itemId());
        assertEquals(10, dto.quantity());
        assertEquals(InventoryTransactionType.OUT, dto.type());
        assertEquals(InventoryReferenceType.ORDER, dto.referenceType());
        assertEquals(100L, dto.referenceId());
        assertEquals("Order dispatch", dto.reason());
        assertEquals(createdAt, dto.createdAt());
    }

    @Test
    void updateEntity() {
        InventoryItem item = InventoryItem.builder()
                .id(5L)
                .quantity(20)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .build();

        InventoryTransaction transaction =
                InventoryTransaction.builder()
                        .id(1L)
                        .version(3L)
                        .inventoryItem(item)
                        .quantity(5)
                        .transactionType(InventoryTransactionType.IN)
                        .referenceType(InventoryReferenceType.MANUAL)
                        .referenceId(null)
                        .reason("Old reason")
                        .build();

        InventoryTransactionRequest request =
                new InventoryTransactionRequest(
                        8,
                        InventoryTransactionType.ADJUSTMENT,
                        InventoryReferenceType.ORDER,
                        200L,
                        "Stock adjustment"
                );

        mapper.updateEntity(request, transaction);

        assertEquals(8, transaction.getQuantity());
        assertEquals(
                InventoryTransactionType.ADJUSTMENT,
                transaction.getTransactionType()
        );
        assertEquals(
                InventoryReferenceType.ORDER,
                transaction.getReferenceType()
        );
        assertEquals(200L, transaction.getReferenceId());
        assertEquals("Stock adjustment", transaction.getReason());

        assertEquals(1L, transaction.getId());
        assertEquals(3L, transaction.getVersion());
        assertSame(item, transaction.getInventoryItem());
    }
}