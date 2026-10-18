package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankDto;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankRequest;
import com.mftplus.mftonlinestoreee.financials.service.BankService;
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
@Path("/banks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankApi {
    @Inject
    private BankService bankService;

    @GET
    public List<BankDto> getBanks() {
        return bankService.findAll();
    }


    @GET
    @Path("/{id}")
    public BankDto getBank(@PathParam("id") Long id) {
        return bankService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createBank(@NotNull @Valid BankRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(bankService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public BankDto updateBank(@PathParam("id") Long id, @NotNull @Valid BankRequest request) {
        return bankService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBank(@PathParam("id") Long id) {
        bankService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Bank " + id + " not found");
    }
}
