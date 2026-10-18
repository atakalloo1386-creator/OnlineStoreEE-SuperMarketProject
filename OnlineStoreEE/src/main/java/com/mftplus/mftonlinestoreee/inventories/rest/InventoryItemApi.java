package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryItemService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/inventory-items")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryItemApi {

    @Inject
    private InventoryItemService inventoryItemService;


    @GET
    @PermitAll
    public List<InventoryItemDto> getInventoryItems() {
        return inventoryItemService.findAll();
    }


    @GET
    @Path("/low-stock")
    @PermitAll
    public List<InventoryItemDto> getLowStock() {
        return inventoryItemService.findLowStock();
    }


    @GET
    @Path("/page")
    @PermitAll
    public PageResult<InventoryItemDto> getInventoryItemsPage(
            @QueryParam("page")
            @DefaultValue("0")
            @Min(0) int page,

            @QueryParam("size")
            @DefaultValue("10")
            @Min(1)
            @Max(100) int size) {

        return inventoryItemService.findAll(page, size);
    }


    @GET
    @Path("/{id}")
    @PermitAll
    public InventoryItemDto getInventoryItem(
            @PathParam("id") Long id) {

        return inventoryItemService.findById(id)
                .orElseThrow(() -> notFound(id));
    }


    @POST
    public Response createInventoryItem(
            @NotNull @Valid InventoryItemRequest request) {

        return Response
                .status(Response.Status.CREATED)
                .entity(inventoryItemService.create(request))
                .build();
    }


    @PUT
    @Path("/{id}")
    public InventoryItemDto updateInventoryItem(
            @PathParam("id") Long id,
            @NotNull @Valid InventoryItemRequest request) {

        return inventoryItemService.update(id, request)
                .orElseThrow(() -> notFound(id));
    }


    @DELETE
    @Path("/{id}")
    public Response deleteInventoryItem(
            @PathParam("id") Long id) {

        inventoryItemService.deleteById(id)
                .orElseThrow(() -> notFound(id));

        return Response.noContent().build();
    }


    private NotFoundException notFound(Long id) {
        return new NotFoundException(
                "Inventory item " + id + " not found");
    }
}