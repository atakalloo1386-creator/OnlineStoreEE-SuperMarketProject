package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.financials.dto.cashpayment.CashPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.service.CashPaymentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/payments/cash")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CashPaymentApi {
    @Inject
    private CashPaymentService cashPaymentService;

    @POST
    public Response createCashPayment(@NotNull @Valid CashPaymentRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(cashPaymentService.create(request))
                .build();
    }
}
