package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.financials.dto.cardpayment.CardPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.service.CardPaymentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/payments/card")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardPaymentApi {
    @Inject
    private CardPaymentService cardPaymentService;

    @POST
    public Response createCardPayment(@NotNull @Valid CardPaymentRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(cardPaymentService.create(request))
                .build();
    }
}
