package com.mftplus.mftonlinestoreee.inventories.service;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.exception.DuplicateInventoryNameException;
import com.mftplus.mftonlinestoreee.inventories.exception.InventoryNotEmptyException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository repository;

    @Mock
    private InventoryItemRepository itemRepository;

    @Spy
    private InventoryMapper mapper =
            Mappers.getMapper(InventoryMapper.class);

    @InjectMocks
    private InventoryService service;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createSavesAndReturnsDto() {
        when(repository.findByName("Main Warehouse"))
                .thenReturn(Optional.empty());

        when(repository.save(any(Inventory.class))).thenAnswer(call -> {
            Inventory inventory = call.getArgument(0);
            inventory.setId(1L);
            return inventory;
        });

        InventoryDto dto = service.create(
                new InventoryRequest("Main Warehouse", "Tehran")
        );

        assertEquals(1L, dto.id());
        assertEquals("Main Warehouse", dto.name());
        assertEquals("Tehran", dto.location());

        verify(repository).save(any(Inventory.class));
    }

    @Test
    void createWithExistingNameFails() {
        when(repository.findByName("Main Warehouse"))
                .thenReturn(Optional.of(inventory(1L, "Main Warehouse", "Tehran")));

        assertThrows(
                DuplicateInventoryNameException.class,
                () -> service.create(
                        new InventoryRequest("Main Warehouse", "Karaj")
                )
        );

        verify(repository, never()).save(any(Inventory.class));
    }

    @Test
    void findByIdReturnsDtoWhenInventoryExists() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(
                        inventory(1L, "Main Warehouse", "Tehran")
                ));

        Optional<InventoryDto> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Main Warehouse", result.get().name());
        assertEquals("Tehran", result.get().location());
    }

    @Test
    void findByIdReturnsEmptyWhenInventoryDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.findById(99L).isEmpty());
    }

    @Test
    void findAllReturnsDtos() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                inventory(1L, "Warehouse A", "Tehran"),
                inventory(2L, "Warehouse B", "Karaj")
        ));

        var result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Warehouse A", result.get(0).name());
        assertEquals("Warehouse B", result.get(1).name());
    }

    @Test
    void updateChangesInventory() {
        Inventory existing = inventory(1L, "Old Warehouse", "Shiraz");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByName("New Warehouse"))
                .thenReturn(Optional.empty());

        Optional<InventoryDto> result = service.update(
                1L,
                new InventoryRequest("New Warehouse", "Tehran")
        );

        assertTrue(result.isPresent());
        assertEquals("New Warehouse", result.get().name());
        assertEquals("Tehran", result.get().location());
        assertEquals(1L, existing.getId());
    }

    @Test
    void updateMayKeepItsOwnName() {
        Inventory existing = inventory(1L, "Main Warehouse", "Tehran");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByName("Main Warehouse"))
                .thenReturn(Optional.of(existing));

        Optional<InventoryDto> result = service.update(
                1L,
                new InventoryRequest("Main Warehouse", "Karaj")
        );

        assertTrue(result.isPresent());
        assertEquals("Main Warehouse", result.get().name());
        assertEquals("Karaj", result.get().location());
    }

    @Test
    void updateToAnotherInventoryNameFails() {
        Inventory existing = inventory(1L, "Warehouse A", "Tehran");
        Inventory another = inventory(2L, "Warehouse B", "Karaj");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByName("Warehouse B"))
                .thenReturn(Optional.of(another));

        assertThrows(
                DuplicateInventoryNameException.class,
                () -> service.update(
                        1L,
                        new InventoryRequest("Warehouse B", "Tehran")
                )
        );
    }

    @Test
    void updateOfMissingInventoryReturnsEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<InventoryDto> result = service.update(
                99L,
                new InventoryRequest("New Warehouse", "Tehran")
        );

        assertTrue(result.isEmpty());
        verify(repository, never()).save(any(Inventory.class));
    }

    @Test
    void deleteRemovesEmptyInventory() {
        Inventory existing = inventory(1L, "Main Warehouse", "Tehran");

        when(itemRepository.countByInventory(1L)).thenReturn(0L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Optional<InventoryDto> result = service.deleteById(1L);

        assertTrue(result.isPresent());
        assertEquals("Main Warehouse", result.get().name());

        verify(repository).delete(existing);
    }

    @Test
    void deleteInventoryWithItemsFails() {
        when(itemRepository.countByInventory(1L)).thenReturn(2L);

        assertThrows(
                InventoryNotEmptyException.class,
                () -> service.deleteById(1L)
        );

        verify(repository, never()).delete(any(Inventory.class));
        verify(repository, never()).findById(1L);
    }

    @Test
    void deleteMissingInventoryReturnsEmptyWhenItIsEmpty() {
        when(itemRepository.countByInventory(99L)).thenReturn(0L);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.deleteById(99L).isEmpty());
        verify(repository, never()).delete(any(Inventory.class));
    }

    private InventoryRequest request(String name, String location) {
        return new InventoryRequest(name, location);
    }

    private Inventory inventory(Long id, String name, String location) {
        return Inventory.builder()
                .id(id)
                .name(name)
                .location(location)
                .build();
    }
}
