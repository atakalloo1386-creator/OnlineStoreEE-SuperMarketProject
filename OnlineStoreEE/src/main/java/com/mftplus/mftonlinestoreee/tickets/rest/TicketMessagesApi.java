package com.mftplus.mftonlinestoreee.tickets.rest;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.service.TicketMessageService;
import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/tickets/{ticketId}/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketMessagesApi {
    @Inject
    private TicketMessageService service;
    @Inject
    private TicketService ticketService;

    @GET
    public List<TicketMessageDto> findAll(@PathParam("ticketId") Long ticketId) {
        ticketService.findById(ticketId).orElseThrow(() -> notFound(ticketId));
        return service.findByTicket(ticketId);
    }

    @POST
    public Response create(@PathParam("ticketId") Long ticketId, @NotNull @Valid TicketMessageRequest request) {
        if (!ticketId.equals(request.ticketId())) {
            throw new BadRequestException("ticketId must match the URL");
        }
        return Response.status(Response.Status.CREATED)
                .entity(service.addMessage(ticketId, request).orElseThrow(() -> notFound(ticketId)))
                .build();
    }

    private NotFoundException notFound(Long ticketId) {
        return new NotFoundException("Ticket " + ticketId + " not found");
    }
}
