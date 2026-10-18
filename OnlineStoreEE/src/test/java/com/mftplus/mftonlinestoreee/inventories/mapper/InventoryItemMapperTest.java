package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemMapperTest {

    private final InventoryItemMapper mapper =
            Mappers.getMapper(InventoryItemMapper.class);

    @Test
    void toEntity() {
        InventoryItemRequest request = new InventoryItemRequest(
                10L,
                20L,
                5,
                "BATCH-001",
                LocalDate.of(2027, 12, 31)
        );

        InventoryItem item = mapper.toEntity(request);

        assertNull(item.getId());
        assertNull(item.getInventory());
        assertNull(item.getProduct());

        assertEquals(0, item.getQuantity());
        assertEquals(5, item.getMinimumQuantity());
        assertEquals("BATCH-001", item.getBatchNumber());
        assertEquals(
                LocalDate.of(2027, 12, 31),
                item.getExpirationDate()
        );
    }

    @Test
    void toDto() {
        Inventory inventory = Inventory.builder()
                .id(10L)
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        Product product = Product.builder()
                .id(20L)
                .name("Laptop")
                .build();

        InventoryItem item = InventoryItem.builder()
                .id(1L)
                .inventory(inventory)
                .product(product)
                .quantity(15)
                .minimumQuantity(5)
                .batchNumber("BATCH-001")
                .expirationDate(LocalDate.of(2027, 12, 31))
                .build();

        InventoryItemDto dto = mapper.toDto(item);

        assertEquals(1L, dto.id());
        assertEquals(10L, dto.inventoryId());
        assertEquals(20L, dto.productId());
        assertEquals("Laptop", dto.productName());
        assertEquals(15, dto.quantity());
        assertEquals(5, dto.minimumQuantity());
        assertEquals("BATCH-001", dto.batchNumber());
        assertEquals(
                LocalDate.of(2027, 12, 31),
                dto.expirationDate()
        );
    }

    @Test
    void updateEntity() {
        Inventory inventory = Inventory.builder()
                .id(10L)
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        Product product = Product.builder()
                .id(20L)
                .name("Laptop")
                .build();

        InventoryItem item = InventoryItem.builder()
                .id(1L)
                .version(3L)
                .inventory(inventory)
                .product(product)
                .quantity(15)
                .minimumQuantity(2)
                .batchNumber("OLD-BATCH")
                .expirationDate(LocalDate.of(2027, 1, 1))
                .build();

        InventoryItemRequest request = new InventoryItemRequest(
                30L,
                40L,
                5,
                "NEW-BATCH",
                LocalDate.of(2028, 6, 30)
        );

        mapper.updateEntity(request, item);

        assertEquals(5, item.getMinimumQuantity());
        assertEquals("NEW-BATCH", item.getBatchNumber());
        assertEquals(
                LocalDate.of(2028, 6, 30),
                item.getExpirationDate()
        );

        assertEquals(1L, item.getId());
        assertEquals(3L, item.getVersion());
        assertEquals(15, item.getQuantity());

        assertSame(inventory, item.getInventory());
        assertSame(product, item.getProduct());
    }
}