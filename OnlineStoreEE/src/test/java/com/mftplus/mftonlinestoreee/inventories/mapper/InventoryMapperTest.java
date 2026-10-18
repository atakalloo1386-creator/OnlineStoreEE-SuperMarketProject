package com.mftplus.mftonlinestoreee.inventories.mapper;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private final InventoryMapper mapper =
            Mappers.getMapper(InventoryMapper.class);

    @Test
    void toEntity() {
        InventoryRequest request =
                new InventoryRequest("Main Warehouse", "Tehran");

        Inventory inventory = mapper.toEntity(request);

        assertNull(inventory.getId());
        assertEquals("Main Warehouse", inventory.getName());
        assertEquals("Tehran", inventory.getLocation());
    }

    @Test
    void toDto() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        InventoryDto dto = mapper.toDto(inventory);

        assertEquals(1L, dto.id());
        assertEquals("Main Warehouse", dto.name());
        assertEquals("Tehran", dto.location());
    }

    @Test
    void updateEntity() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .version(3L)
                .name("Old Warehouse")
                .location("Shiraz")
                .build();

        mapper.updateEntity(
                new InventoryRequest("New Warehouse", "Tehran"),
                inventory
        );

        assertEquals("New Warehouse", inventory.getName());
        assertEquals("Tehran", inventory.getLocation());
        assertEquals(1L, inventory.getId());
        assertEquals(3L, inventory.getVersion());
    }
}