package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryService;
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
@Path("/inventories")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryApi {

    @Inject
    private InventoryService inventoryService;

    @GET
    @PermitAll
    public List<InventoryDto> getInventories() {
        return inventoryService.findAll();
    }


    @GET
    @Path("/page")
    @PermitAll
    public PageResult<InventoryDto> getInventoriesPage(
            @QueryParam("page")
            @DefaultValue("0")
            @Min(0) int page,

            @QueryParam("size")
            @DefaultValue("10")
            @Min(1)
            @Max(100) int size) {

        return inventoryService.findAll(page, size);
    }


    @GET
    @Path("/{id}")
    @PermitAll
    public InventoryDto getInventory(@PathParam("id") Long id) {

        return inventoryService.findById(id)
                .orElseThrow(() -> notFound(id));
    }


    @POST
    public Response createInventory(
            @NotNull @Valid InventoryRequest request) {

        return Response
                .status(Response.Status.CREATED)
                .entity(inventoryService.create(request))
                .build();
    }


    @PUT
    @Path("/{id}")
    public InventoryDto updateInventory(
            @PathParam("id") Long id,
            @NotNull @Valid InventoryRequest request) {

        return inventoryService.update(id, request)
                .orElseThrow(() -> notFound(id));
    }


    @DELETE
    @Path("/{id}")
    public Response deleteInventory(
            @PathParam("id") Long id) {

        inventoryService.deleteById(id)
                .orElseThrow(() -> notFound(id));

        return Response.noContent().build();
    }


    private NotFoundException notFound(Long id) {
        return new NotFoundException(
                "Inventory " + id + " not found");
    }
}