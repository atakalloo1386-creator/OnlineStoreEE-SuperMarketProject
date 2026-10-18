package com.mftplus.mftonlinestoreee.financials.rest;

import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentDto;
import com.mftplus.mftonlinestoreee.financials.service.PaymentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;


@RequestScoped
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentApi {
    @Inject
    private PaymentService paymentService;


    @GET
    public List<PaymentDto> getPayments(@QueryParam("orderId") Long orderId) {
        if (orderId == null) {
            return paymentService.findAll();
        }
        return paymentService.findByOrder(orderId);
    }

    @GET
    @Path("/{id}")
    public PaymentDto getPayment(@PathParam("id") Long id) {
        return paymentService.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment " + id + " not found"));
    }
}
