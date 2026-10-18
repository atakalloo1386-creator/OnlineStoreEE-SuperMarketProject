package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionDto;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionRequest;
import com.mftplus.mftonlinestoreee.financials.service.TransactionService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@RequestScoped
@Path("/bankaccounts/{bankAccountId}/transactions")

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionApi {
    @Inject
    private TransactionService transactionService;

    @GET
    public List<TransactionDto> getTransactions(@PathParam("bankAccountId") Long bankAccountId) {
        return transactionService.findByBankAccount(bankAccountId);
    }

    @POST
    public Response registerTransaction(@PathParam("bankAccountId") Long bankAccountId,
                                        @NotNull @Valid TransactionRequest request) {
        TransactionDto transaction = transactionService.register(bankAccountId, request)
                .orElseThrow(() -> new NotFoundException("Bank account " + bankAccountId + " not found"));
        return Response
                .status(Response.Status.CREATED)
                .entity(transaction)
                .build();
    }
}
