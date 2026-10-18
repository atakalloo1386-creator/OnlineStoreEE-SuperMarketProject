package com.mftplus.mftonlinestoreee.inventories.service;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import com.mftplus.mftonlinestoreee.inventories.exception.InsufficientStockException;
import com.mftplus.mftonlinestoreee.inventories.exception.InvalidQuantityException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryTransactionMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryTransactionServiceTest {

    @Mock
    private InventoryTransactionRepository repository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Spy
    private InventoryTransactionMapper mapper =
            Mappers.getMapper(InventoryTransactionMapper.class);

    @InjectMocks
    private InventoryTransactionService service;

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
    void findByItemReturnsTransactionDtos() {
        InventoryItem item = item(5L, 20);

        InventoryTransaction first = transaction(
                1L, item, 10,
                InventoryTransactionType.IN,
                "Stock received"
        );

        InventoryTransaction second = transaction(
                2L, item, 3,
                InventoryTransactionType.OUT,
                "Stock dispatched"
        );

        when(repository.findByItem(5L))
                .thenReturn(Arrays.asList(first, second));

        java.util.List<InventoryTransactionDto> result =
                service.findByItem(5L);

        assertEquals(2, result.size());
        assertEquals(5L, result.get(0).itemId());
        assertEquals(InventoryTransactionType.IN, result.get(0).type());
        assertEquals("Stock received", result.get(0).reason());
        assertEquals(InventoryTransactionType.OUT, result.get(1).type());
        assertEquals("Stock dispatched", result.get(1).reason());
    }

    @Test
    void findByItemReturnsEmptyListWhenNoTransactionsExist() {
        when(repository.findByItem(5L))
                .thenReturn(Collections.emptyList());

        assertTrue(service.findByItem(5L).isEmpty());
    }

    @Test
    void registerIncreasesStockAndSavesTransaction() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));

        when(repository.save(any(InventoryTransaction.class)))
                .thenAnswer(call -> {
                    InventoryTransaction transaction = call.getArgument(0);
                    transaction.setId(1L);
                    return transaction;
                });

        InventoryTransactionRequest request = request(
                5,
                InventoryTransactionType.IN,
                InventoryReferenceType.MANUAL,
                null,
                "Stock received"
        );

        Optional<InventoryTransactionDto> result =
                service.register(5L, request);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals(5L, result.get().itemId());
        assertEquals(5, result.get().quantity());
        assertEquals(InventoryTransactionType.IN, result.get().type());
        assertEquals("Stock received", result.get().reason());

        assertEquals(15, item.getQuantity());

        verify(inventoryItemRepository).update(item);
        verify(repository).save(any(InventoryTransaction.class));
    }

    @Test
    void registerOutDecreasesStock() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));
        when(repository.save(any(InventoryTransaction.class)))
                .thenAnswer(call -> {
                    InventoryTransaction transaction = call.getArgument(0);
                    transaction.setId(2L);
                    return transaction;
                });

        Optional<InventoryTransactionDto> result = service.register(
                5L,
                request(
                        4,
                        InventoryTransactionType.OUT,
                        InventoryReferenceType.ORDER,
                        100L,
                        "Order dispatched"
                )
        );

        assertTrue(result.isPresent());
        assertEquals(6, item.getQuantity());
        assertEquals(InventoryTransactionType.OUT, result.get().type());
        assertEquals(InventoryReferenceType.ORDER, result.get().referenceType());
        assertEquals(100L, result.get().referenceId());

        verify(inventoryItemRepository).update(item);
        verify(repository).save(any(InventoryTransaction.class));
    }

    @Test
    void registerReturnIncreasesStock() {
        InventoryItem item = item(5L, 6);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));
        when(repository.save(any(InventoryTransaction.class)))
                .thenAnswer(call -> call.getArgument(0));

        Optional<InventoryTransactionDto> result = service.register(
                5L,
                request(
                        2,
                        InventoryTransactionType.RETURN,
                        InventoryReferenceType.ORDER,
                        100L,
                        "Returned item"
                )
        );

        assertTrue(result.isPresent());
        assertEquals(8, item.getQuantity());
        assertEquals(InventoryTransactionType.RETURN, result.get().type());
    }

    @Test
    void registerAdjustmentChangesStockBySignedQuantity() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));
        when(repository.save(any(InventoryTransaction.class)))
                .thenAnswer(call -> call.getArgument(0));

        Optional<InventoryTransactionDto> result = service.register(
                5L,
                request(
                        -3,
                        InventoryTransactionType.ADJUSTMENT,
                        InventoryReferenceType.MANUAL,
                        null,
                        "Stock correction"
                )
        );

        assertTrue(result.isPresent());
        assertEquals(7, item.getQuantity());
        assertEquals(
                InventoryTransactionType.ADJUSTMENT,
                result.get().type()
        );
    }

    @Test
    void registerReturnsEmptyWhenItemDoesNotExist() {
        when(inventoryItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<InventoryTransactionDto> result = service.register(
                99L,
                request(
                        5,
                        InventoryTransactionType.IN,
                        InventoryReferenceType.MANUAL,
                        null,
                        "Stock received"
                )
        );

        assertTrue(result.isEmpty());

        verify(inventoryItemRepository, never()).update(any(InventoryItem.class));
        verify(repository, never()).save(any(InventoryTransaction.class));
    }

    @Test
    void registerInWithZeroQuantityFails() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));

        assertThrows(
                InvalidQuantityException.class,
                () -> service.register(
                        5L,
                        request(
                                0,
                                InventoryTransactionType.IN,
                                InventoryReferenceType.MANUAL,
                                null,
                                "Invalid incoming quantity"
                        )
                )
        );

        assertEquals(10, item.getQuantity());
        verify(inventoryItemRepository, never()).update(any(InventoryItem.class));
        verify(repository, never()).save(any(InventoryTransaction.class));
    }

    @Test
    void registerAdjustmentWithZeroQuantityFails() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));

        assertThrows(
                InvalidQuantityException.class,
                () -> service.register(
                        5L,
                        request(
                                0,
                                InventoryTransactionType.ADJUSTMENT,
                                InventoryReferenceType.MANUAL,
                                null,
                                "Zero adjustment"
                        )
                )
        );

        verify(inventoryItemRepository, never()).update(any(InventoryItem.class));
        verify(repository, never()).save(any(InventoryTransaction.class));
    }

    @Test
    void registerOutWithInsufficientStockFails() {
        InventoryItem item = item(5L, 3);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));

        assertThrows(
                InsufficientStockException.class,
                () -> service.register(
                        5L,
                        request(
                                5,
                                InventoryTransactionType.OUT,
                                InventoryReferenceType.ORDER,
                                100L,
                                "Order exceeds stock"
                        )
                )
        );

        assertEquals(3, item.getQuantity());
        verify(inventoryItemRepository, never()).update(any(InventoryItem.class));
        verify(repository, never()).save(any(InventoryTransaction.class));
    }

    @Test
    void registerOutWithNegativeQuantityFails() {
        InventoryItem item = item(5L, 10);

        when(inventoryItemRepository.findById(5L))
                .thenReturn(Optional.of(item));

        assertThrows(
                InvalidQuantityException.class,
                () -> service.register(
                        5L,
                        request(
                                -2,
                                InventoryTransactionType.OUT,
                                InventoryReferenceType.MANUAL,
                                null,
                                "Invalid outgoing quantity"
                        )
                )
        );

        verify(repository, never()).save(any(InventoryTransaction.class));
    }

    private InventoryItem item(Long id, int quantity) {
        return InventoryItem.builder()
                .id(id)
                .quantity(quantity)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .build();
    }

    private InventoryTransaction transaction(
            Long id,
            InventoryItem item,
            int quantity,
            InventoryTransactionType type,
            String reason
    ) {
        return InventoryTransaction.builder()
                .id(id)
                .inventoryItem(item)
                .quantity(quantity)
                .transactionType(type)
                .referenceType(InventoryReferenceType.MANUAL)
                .reason(reason)
                .build();
    }

    private InventoryTransactionRequest request(
            Integer quantity,
            InventoryTransactionType type,
            InventoryReferenceType referenceType,
            Long referenceId,
            String reason
    ) {
        return new InventoryTransactionRequest(
                quantity,
                type,
                referenceType,
                referenceId,
                reason
        );
    }
}