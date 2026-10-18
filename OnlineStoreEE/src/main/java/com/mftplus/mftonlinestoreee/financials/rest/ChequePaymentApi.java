package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentDto;
import com.mftplus.mftonlinestoreee.financials.service.ChequePaymentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/payments/cheque")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChequePaymentApi {
    @Inject
    private ChequePaymentService chequePaymentService;

    @POST
    public Response createChequePayment(@NotNull @Valid ChequePaymentRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(chequePaymentService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public ChequePaymentDto updateChequePayment(@PathParam("id") Long id, @NotNull @Valid ChequePaymentRequest request) {
        return chequePaymentService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteChequePayment(@PathParam("id") Long id) {
        chequePaymentService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }


    @POST
    @Path("/{id}/clear")
    @Consumes(MediaType.WILDCARD)
    public ChequePaymentDto clearChequePayment(@PathParam("id") Long id) {
        return chequePaymentService.clear(id).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/bounce")
    @Consumes(MediaType.WILDCARD)
    public ChequePaymentDto bounceChequePayment(@PathParam("id") Long id) {
        return chequePaymentService.bounce(id).orElseThrow(() -> notFound(id));
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Cheque " + id + " not found");
    }
}
