package com.mftplus.mftonlinestoreee.tickets.rest;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.service.TicketMessageService;
import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/ticket_messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketMessageApi {

    @Inject
    private TicketMessageService ticketMessageService;

    @Inject
    private TicketService ticketService;

    @GET
    @PermitAll
    @Path("/{id}")
    public List<TicketMessageDto> getMessages(@PathParam("id") Long id) {
        if (ticketService.findById(id).isEmpty()) {
            throw notFound(id);
        }
        return ticketMessageService.findByTicket(id);
    }

    @POST
    @PermitAll
    @Path("/{id}")
    public Response createMessage(
            @PathParam("id") Long id,
            @Valid @NotNull TicketMessageRequest request) {

        TicketMessageDto created = ticketMessageService.addMessage(id, request)
                .orElseThrow(() -> notFound(id));

        return Response
                .status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Ticket Message ID " + id + " not found");
    }
}
