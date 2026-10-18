package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryApiTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryApi api;

    private AutoCloseable mocks;

    private final InventoryRequest request =
            new InventoryRequest("Main Warehouse", "Tehran");

    private final InventoryDto inventory =
            new InventoryDto(1L, "Main Warehouse", "Tehran");

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getInventoriesReturnsTheList() {
        when(inventoryService.findAll())
                .thenReturn(List.of(inventory));

        assertEquals(
                List.of(inventory),
                api.getInventories()
        );

        verify(inventoryService).findAll();
    }

    @Test
    void getInventoriesPageReturnsThePageResult() {
        PageResult<InventoryDto> pageResult = mock(PageResult.class);

        when(inventoryService.findAll(0, 10))
                .thenReturn(pageResult);

        assertSame(
                pageResult,
                api.getInventoriesPage(0, 10)
        );

        verify(inventoryService).findAll(0, 10);
    }

    @Test
    void getInventoryReturnsTheDto() {
        when(inventoryService.findById(1L))
                .thenReturn(Optional.of(inventory));

        assertEquals(inventory, api.getInventory(1L));

        verify(inventoryService).findById(1L);
    }

    @Test
    void getMissingInventoryThrowsNotFound() {
        when(inventoryService.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.getInventory(99L)
        );
    }

    @Test
    void createAnswers201WithTheCreatedDto() {
        when(inventoryService.create(request))
                .thenReturn(inventory);

        Response response = api.createInventory(request);

        assertEquals(201, response.getStatus());
        assertEquals(inventory, response.getEntity());

        verify(inventoryService).create(request);

        response.close();
    }

    @Test
    void updateReturnsTheUpdatedDto() {
        when(inventoryService.update(1L, request))
                .thenReturn(Optional.of(inventory));

        assertEquals(
                inventory,
                api.updateInventory(1L, request)
        );

        verify(inventoryService).update(1L, request);
    }

    @Test
    void updateOfMissingInventoryThrowsNotFound() {
        when(inventoryService.update(99L, request))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.updateInventory(99L, request)
        );
    }

    @Test
    void deleteAnswers204() {
        when(inventoryService.deleteById(1L))
                .thenReturn(Optional.of(inventory));

        Response response = api.deleteInventory(1L);

        assertEquals(204, response.getStatus());

        verify(inventoryService).deleteById(1L);

        response.close();
    }

    @Test
    void deleteOfMissingInventoryThrowsNotFound() {
        when(inventoryService.deleteById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> api.deleteInventory(99L)
        );
    }
}
