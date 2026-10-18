package com.mftplus.mftonlinestoreee.delivery.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryDto;
import com.mftplus.mftonlinestoreee.delivery.dto.DeliveryRequest;
import com.mftplus.mftonlinestoreee.delivery.service.DeliveryService;
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
@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryApi {
    @Inject
    private DeliveryService service;

    @GET
    public List<DeliveryDto> findAll(@QueryParam("courierId") Long courierId) {
        return courierId == null ? service.findAll() : service.findByCourier(courierId);
    }

    @GET
    @Path("/page")
    public PageResult<DeliveryDto> findPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                           @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public DeliveryDto findById(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response create(@NotNull @Valid DeliveryRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("/{id}")
    public DeliveryDto update(@PathParam("id") Long id, @NotNull @Valid DeliveryRequest request) {
        return service.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/send")
    public DeliveryDto send(@PathParam("id") Long id) {
        return service.send(id);
    }

    @POST
    @Path("/{id}/confirm")
    public DeliveryDto confirm(@PathParam("id") Long id) {
        return service.confirmDelivery(id);
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Delivery " + id + " not found");
    }
}
