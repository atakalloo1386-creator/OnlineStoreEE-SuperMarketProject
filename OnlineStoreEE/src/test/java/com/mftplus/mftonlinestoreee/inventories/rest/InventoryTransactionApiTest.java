package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryTransactionService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryTransactionApiTest {

    @Mock
    private InventoryTransactionService inventoryTransactionService;

    @InjectMocks
    private InventoryTransactionApi api;

    private AutoCloseable mocks;

    private final InventoryTransactionRequest request =
            new InventoryTransactionRequest(
                    5,
                    InventoryTransactionType.IN,
                    InventoryReferenceType.MANUAL,
                    null,
                    "Initial stock"
            );

    private final InventoryTransactionDto transaction =
            new InventoryTransactionDto(
                    1L,
                    10L,
                    5,
                    InventoryTransactionType.IN,
                    InventoryReferenceType.MANUAL,
                    null,
                    "Initial stock",
                    LocalDateTime.of(2026, 10, 11, 10, 0)
            );

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getTransactionsReturnsTheList() {
        when(inventoryTransactionService.findByItem(10L))
                .thenReturn(List.of(transaction));

        assertEquals(
                List.of(transaction),
                api.getTransactions(10L)
        );

        verify(inventoryTransactionService).findByItem(10L);
    }

    @Test
    void getTransactionsReturnsEmptyListWhenThereAreNoTransactions() {
        when(inventoryTransactionService.findByItem(10L))
                .thenReturn(List.of());

        assertTrue(api.getTransactions(10L).isEmpty());

        verify(inventoryTransactionService).findByItem(10L);
    }

    @Test
    void registerTransactionAnswers201WithTheCreatedDto() {
        when(inventoryTransactionService.register(10L, request))
                .thenReturn(Optional.of(transaction));

        Response response = api.registerTransaction(10L, request);

        assertEquals(201, response.getStatus());
        assertEquals(transaction, response.getEntity());

        verify(inventoryTransactionService)
                .register(10L, request);

        response.close();
    }

    @Test
    void registerTransactionForMissingItemThrowsNotFound() {
        when(inventoryTransactionService.register(99L, request))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.registerTransaction(99L, request)
        );

        verify(inventoryTransactionService)
                .register(99L, request);
    }
}

