package com.mftplus.mftonlinestoreee.delivery.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierDto;
import com.mftplus.mftonlinestoreee.delivery.dto.CourierRequest;
import com.mftplus.mftonlinestoreee.delivery.service.CourierService;
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
@Path("/couriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourierApi {
    @Inject
    private CourierService service;

    @GET
    public List<CourierDto> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<CourierDto> findPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                          @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public CourierDto findById(@PathParam("id") Long id) {
        return service.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response create(@NotNull @Valid CourierRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("/{id}")
    public CourierDto update(@PathParam("id") Long id, @NotNull @Valid CourierRequest request) {
        return service.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Courier " + id + " not found");
    }
}
