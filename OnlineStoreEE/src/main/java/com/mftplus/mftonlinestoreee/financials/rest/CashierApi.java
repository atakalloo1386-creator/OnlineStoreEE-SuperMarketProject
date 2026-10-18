package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierDto;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierRequest;
import com.mftplus.mftonlinestoreee.financials.service.CashierService;
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
@Path("/cashiers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CashierApi {
    @Inject
    private CashierService cashierService;

    @GET
    public List<CashierDto> getCashiers() {
        return cashierService.findAll();
    }

    @GET
    @Path("/{id}")
    public CashierDto getCashier(@PathParam("id") Long id) {
        return cashierService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createCashier(@NotNull @Valid CashierRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(cashierService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public CashierDto updateCashier(@PathParam("id") Long id, @NotNull @Valid CashierRequest request) {
        return cashierService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCashier(@PathParam("id") Long id) {
        cashierService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Cashier " + id + " not found");
    }
}
