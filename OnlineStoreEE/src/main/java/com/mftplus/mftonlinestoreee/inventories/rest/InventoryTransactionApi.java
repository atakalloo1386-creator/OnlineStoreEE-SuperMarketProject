package com.mftplus.mftonlinestoreee.inventories.rest;

import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryTransactionService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/inventory-items/{itemId}/transactions")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryTransactionApi {

    @Inject
    private InventoryTransactionService inventoryTransactionService;


    @GET
    @PermitAll
    public List<InventoryTransactionDto> getTransactions(
            @PathParam("itemId") Long itemId) {

        return inventoryTransactionService.findByItem(itemId);
    }


    @POST
    public Response registerTransaction(
            @PathParam("itemId") Long itemId,
            @NotNull @Valid InventoryTransactionRequest request) {

        return Response
                .status(Response.Status.CREATED)
                .entity(
                        inventoryTransactionService
                                .register(itemId, request)
                                .orElseThrow(() ->
                                        notFound(itemId))
                )
                .build();
    }


    private NotFoundException notFound(Long itemId) {
        return new NotFoundException(
                "Inventory item " + itemId + " not found");
    }
}