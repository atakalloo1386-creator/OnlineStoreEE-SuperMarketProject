package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountDto;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountRequest;
import com.mftplus.mftonlinestoreee.financials.service.BankAccountService;
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
@Path("/bankaccounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankAccountApi {
    @Inject
    private BankAccountService bankAccountService;


    @GET
    @Path("/{bankId}")
    public List<BankAccountDto> getBankAccounts(@PathParam("bankId") Long bankId) {
        if (bankId == null) {
            return bankAccountService.findAll();
        }
        return bankAccountService.findByBank(bankId);
    }

    @GET
    @Path("/{id}")
    public BankAccountDto getBankAccount(@PathParam("id") Long id) {
        return bankAccountService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createBankAccount(@NotNull @Valid BankAccountRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(bankAccountService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateBankAccount(@PathParam("id") Long id, @NotNull @Valid BankAccountRequest request) {
        return Response
                .status(Response.Status.OK)
                .entity(bankAccountService.update(id, request))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBankAccount(@PathParam("id") Long id) {
        bankAccountService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Bank account " + id + " not found");
    }
}
