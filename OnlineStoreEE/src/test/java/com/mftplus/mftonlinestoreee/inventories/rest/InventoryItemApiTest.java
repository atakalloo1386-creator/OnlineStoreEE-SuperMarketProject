package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryItemService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryItemApiTest {

    @Mock
    private InventoryItemService inventoryItemService;

    @InjectMocks
    private InventoryItemApi api;

    private AutoCloseable mocks;

    private final InventoryItemRequest request =
            new InventoryItemRequest(
                    10L,
                    20L,
                    3,
                    "BATCH-01",
                    LocalDate.of(2027, 12, 31)
            );

    private final InventoryItemDto item =
            new InventoryItemDto(
                    1L,
                    10L,
                    20L,
                    "Laptop",
                    5,
                    3,
                    "BATCH-01",
                    LocalDate.of(2027, 12, 31)
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
    void getInventoryItemsReturnsTheList() {
        when(inventoryItemService.findAll())
                .thenReturn(List.of(item));

        assertEquals(
                List.of(item),
                api.getInventoryItems()
        );

        verify(inventoryItemService).findAll();
    }

    @Test
    void getLowStockReturnsTheList() {
        when(inventoryItemService.findLowStock())
                .thenReturn(List.of(item));

        assertEquals(
                List.of(item),
                api.getLowStock()
        );

        verify(inventoryItemService).findLowStock();
    }

    @Test
    void getInventoryItemsPageReturnsThePageResult() {
        PageResult<InventoryItemDto> pageResult =
                mock(PageResult.class);

        when(inventoryItemService.findAll(0, 10))
                .thenReturn(pageResult);

        assertSame(
                pageResult,
                api.getInventoryItemsPage(0, 10)
        );

        verify(inventoryItemService).findAll(0, 10);
    }

    @Test
    void getInventoryItemReturnsTheDto() {
        when(inventoryItemService.findById(1L))
                .thenReturn(Optional.of(item));

        assertEquals(item, api.getInventoryItem(1L));

        verify(inventoryItemService).findById(1L);
    }

    @Test
    void getMissingInventoryItemThrowsNotFound() {
        when(inventoryItemService.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.getInventoryItem(99L)
        );
    }

    @Test
    void createAnswers201WithTheCreatedDto() {
        when(inventoryItemService.create(request))
                .thenReturn(item);

        Response response = api.createInventoryItem(request);

        assertEquals(201, response.getStatus());
        assertEquals(item, response.getEntity());

        verify(inventoryItemService).create(request);

        response.close();
    }

    @Test
    void updateReturnsTheUpdatedDto() {
        when(inventoryItemService.update(1L, request))
                .thenReturn(Optional.of(item));

        assertEquals(
                item,
                api.updateInventoryItem(1L, request)
        );

        verify(inventoryItemService).update(1L, request);
    }

    @Test
    void updateOfMissingInventoryItemThrowsNotFound() {
        when(inventoryItemService.update(99L, request))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.updateInventoryItem(99L, request)
        );
    }

    @Test
    void deleteAnswers204() {
        when(inventoryItemService.deleteById(1L))
                .thenReturn(Optional.of(item));

        Response response = api.deleteInventoryItem(1L);

        assertEquals(204, response.getStatus());

        verify(inventoryItemService).deleteById(1L);

        response.close();
    }

    @Test
    void deleteOfMissingInventoryItemThrowsNotFound() {
        when(inventoryItemService.deleteById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.deleteInventoryItem(99L)
        );
    }
}

