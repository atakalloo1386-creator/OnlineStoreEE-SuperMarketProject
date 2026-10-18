package com.mftplus.mftonlinestoreee.tickets.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import jakarta.annotation.security.PermitAll;
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
@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketApi {

    @Inject
    private TicketService ticketService;

    @GET
    @PermitAll
    public List<TicketDto> getTickets(
            @QueryParam("userId") Long userId) {
        if (userId != null){
            return ticketService.findByUser(userId);
        }
        return ticketService.findAll();

    }

    @GET
    @Path("/page")
    @PermitAll
    public PageResult<TicketDto> getTicketsPage(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size){
        return ticketService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public TicketDto getTicket(
            @PathParam("id") Long id){
        return ticketService.findById(id)
                .orElseThrow(()->notFound(id));
    }

    @POST
    public Response createTicket(
            @Valid @NotNull TicketRequest request){
        return Response
                .status(Response.Status.CREATED)
                .entity(ticketService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public TicketDto updateTicket(
            @PathParam("id") Long id,
            @NotNull @Valid TicketRequest request){
        return ticketService.update(id, request)
                .orElseThrow(()->notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTicket(
            @PathParam("id") Long id){
        ticketService.deleteById(id)
                .orElseThrow(()->notFound(id));
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/close")
    public TicketDto closeTicket(@PathParam("id") Long id) {
        return ticketService.close(id)
                .orElseThrow(() -> notFound(id));
    }

    private NotFoundException notFound(Long id){
        return new NotFoundException("Ticket ID" + id + " not found");
    }
}
