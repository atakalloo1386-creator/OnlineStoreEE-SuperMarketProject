package com.mftplus.mftonlinestoreee.inventories.service;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.exception.DuplicateInventoryItemException;
import com.mftplus.mftonlinestoreee.inventories.exception.InventoryNotFoundException;
import com.mftplus.mftonlinestoreee.inventories.exception.ProductNotFoundException;
import com.mftplus.mftonlinestoreee.inventories.exception.StockNotEmptyException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryItemMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryRepository;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import com.mftplus.mftonlinestoreee.products.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryItemServiceTest {

    @Mock
    private InventoryItemRepository repository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private InventoryItemMapper mapper =
            Mappers.getMapper(InventoryItemMapper.class);

    @InjectMocks
    private InventoryItemService service;

    private AutoCloseable mocks;

    private Inventory inventory;
    private Product product;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        inventory = Inventory.builder()
                .id(10L)
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        product = Product.builder()
                .id(20L)
                .name("Laptop")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createSavesItemWithZeroQuantity() {
        InventoryItemRequest request = request(10L, 20L, 5, "BATCH-001");

        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));
        when(repository.findByInventoryAndProduct(10L, 20L))
                .thenReturn(Optional.empty());

        when(repository.save(any(InventoryItem.class))).thenAnswer(call -> {
            InventoryItem item = call.getArgument(0);
            item.setId(1L);
            return item;
        });

        InventoryItemDto dto = service.create(request);

        assertEquals(1L, dto.id());
        assertEquals(10L, dto.inventoryId());
        assertEquals(20L, dto.productId());
        assertEquals("Laptop", dto.productName());
        assertEquals(0, dto.quantity());
        assertEquals(5, dto.minimumQuantity());
        assertEquals("BATCH-001", dto.batchNumber());

        verify(repository).save(any(InventoryItem.class));
    }

    @Test
    void createFailsWhenInventoryDoesNotExist() {
        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> service.create(request(10L, 20L, 5, "BATCH-001"))
        );

        verify(repository, never()).save(any(InventoryItem.class));
    }

    @Test
    void createFailsWhenProductDoesNotExist() {
        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.create(request(10L, 20L, 5, "BATCH-001"))
        );

        verify(repository, never()).save(any(InventoryItem.class));
    }

    @Test
    void createFailsWhenProductAlreadyExistsInInventory() {
        InventoryItem existing = item(1L, inventory, product, 10);

        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));
        when(repository.findByInventoryAndProduct(10L, 20L))
                .thenReturn(Optional.of(existing));

        assertThrows(
                DuplicateInventoryItemException.class,
                () -> service.create(request(10L, 20L, 5, "BATCH-002"))
        );

        verify(repository, never()).save(any(InventoryItem.class));
    }

    @Test
    void findByIdReturnsDtoWhenItemExists() {
        InventoryItem existing = item(1L, inventory, product, 12);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Optional<InventoryItemDto> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals(12, result.get().quantity());
        assertEquals("Laptop", result.get().productName());
    }

    @Test
    void findByIdReturnsEmptyWhenItemDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.findById(99L).isEmpty());
    }

    @Test
    void findAllReturnsDtos() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                item(1L, inventory, product, 10),
                item(2L, inventory, product, 15)
        ));

        java.util.List<InventoryItemDto> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).quantity());
        assertEquals(15, result.get(1).quantity());
    }

    @Test
    void updateChangesItemDetails() {
        InventoryItem existing = item(1L, inventory, product, 15);

        Inventory newInventory = Inventory.builder()
                .id(30L)
                .name("Secondary Warehouse")
                .location("Karaj")
                .build();

        Product newProduct = Product.builder()
                .id(40L)
                .name("Monitor")
                .build();

        when(inventoryRepository.findById(30L))
                .thenReturn(Optional.of(newInventory));
        when(productRepository.findById(40L))
                .thenReturn(Optional.of(newProduct));
        when(repository.findByInventoryAndProduct(30L, 40L))
                .thenReturn(Optional.empty());
        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        InventoryItemRequest request =
                request(30L, 40L, 8, "BATCH-NEW");

        Optional<InventoryItemDto> result = service.update(1L, request);

        assertTrue(result.isPresent());
        assertEquals(30L, result.get().inventoryId());
        assertEquals(40L, result.get().productId());
        assertEquals("Monitor", result.get().productName());
        assertEquals(8, result.get().minimumQuantity());
        assertEquals("BATCH-NEW", result.get().batchNumber());

        assertEquals(15, existing.getQuantity());

        verify(repository).update(existing);
    }

    @Test
    void updateMayKeepItsOwnInventoryAndProductCombination() {
        InventoryItem existing = item(1L, inventory, product, 10);

        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));
        when(repository.findByInventoryAndProduct(10L, 20L))
                .thenReturn(Optional.of(existing));
        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        Optional<InventoryItemDto> result = service.update(
                1L,
                request(10L, 20L, 4, "BATCH-UPDATED")
        );

        assertTrue(result.isPresent());
        assertEquals("BATCH-UPDATED", result.get().batchNumber());
        verify(repository).update(existing);
    }

    @Test
    void updateFailsWhenCombinationBelongsToAnotherItem() {
        InventoryItem current = item(1L, inventory, product, 10);
        InventoryItem another = item(2L, inventory, product, 5);

        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));
        when(repository.findByInventoryAndProduct(10L, 20L))
                .thenReturn(Optional.of(another));

        assertThrows(
                DuplicateInventoryItemException.class,
                () -> service.update(
                        1L,
                        request(10L, 20L, 4, "BATCH-UPDATED")
                )
        );

        verify(repository, never()).update(any(InventoryItem.class));
    }

    @Test
    void updateOfMissingItemReturnsEmpty() {
        when(inventoryRepository.findById(10L))
                .thenReturn(Optional.of(inventory));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));
        when(repository.findByInventoryAndProduct(10L, 20L))
                .thenReturn(Optional.empty());
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.update(
                99L,
                request(10L, 20L, 5, "BATCH-001")
        ).isEmpty());

        verify(repository, never()).update(any(InventoryItem.class));
    }

    @Test
    void deleteRemovesItemWhenQuantityIsZero() {
        InventoryItem existing = item(1L, inventory, product, 0);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Optional<InventoryItemDto> result = service.deleteById(1L);

        assertTrue(result.isPresent());
        verify(repository).delete(existing);
    }

    @Test
    void deleteFailsWhenQuantityIsNotZero() {
        InventoryItem existing = item(1L, inventory, product, 5);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(
                StockNotEmptyException.class,
                () -> service.deleteById(1L)
        );

        verify(repository, never()).delete(any(InventoryItem.class));
    }

    @Test
    void deleteMissingItemReturnsEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.deleteById(99L).isEmpty());
        verify(repository, never()).delete(any(InventoryItem.class));
    }

    @Test
    void findByInventoryReturnsMappedItems() {
        when(repository.findByInventory(10L)).thenReturn(
                Collections.singletonList(item(1L, inventory, product, 12))
        );

        java.util.List<InventoryItemDto> result =
                service.findByInventory(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).inventoryId());
        assertEquals("Laptop", result.get(0).productName());
    }

    @Test
    void findLowStockReturnsMappedItems() {
        when(repository.findLowStock()).thenReturn(
                Collections.singletonList(item(1L, inventory, product, 2))
        );

        java.util.List<InventoryItemDto> result = service.findLowStock();

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).quantity());
    }

    private InventoryItemRequest request(
            Long inventoryId,
            Long productId,
            Integer minimumQuantity,
            String batchNumber
    ) {
        return new InventoryItemRequest(
                inventoryId,
                productId,
                minimumQuantity,
                batchNumber,
                LocalDate.of(2027, 12, 31)
        );
    }

    private InventoryItem item(
            Long id,
            Inventory inventory,
            Product product,
            int quantity
    ) {
        return InventoryItem.builder()
                .id(id)
                .inventory(inventory)
                .product(product)
                .quantity(quantity)
                .minimumQuantity(3)
                .batchNumber("BATCH-001")
                .build();
    }
}