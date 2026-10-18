package com.mftplus.mftonlinestoreee.orders.rest;


import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.orders.dto.OrderDto;
import com.mftplus.mftonlinestoreee.orders.dto.OrderRequest;
import com.mftplus.mftonlinestoreee.orders.service.OrderService;
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
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderApi {
    @Inject
    private OrderService orderService;

    @GET
    public List<OrderDto> getOrders() {
        return orderService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<OrderDto> getOrdersPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                              @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size){
        return orderService.findAll(page, size);
    }

    @GET
    @Path("/customer/{id}")
    public List<OrderDto> getOrdersByCustomerId(@PathParam("id") Long id){
        return orderService.findByCustomer(id);
    }

    @GET
    @Path("/{id}")
    public OrderDto getOrder(@PathParam("id") Long id){
        return orderService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createOrder(@NotNull @Valid OrderRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(orderService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") Long id, @NotNull @Valid OrderRequest request) {
        return Response
                .status(Response.Status.OK)
                .entity(orderService.update(id, request).orElseThrow(() -> notFound(id)))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        orderService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/confirm")
    public Response confirmOrder(@PathParam("id") Long id) {
        return Response
                .status(Response.Status.OK)
                .entity(orderService.confirm(id).orElseThrow(() -> notFound(id)))
                .build();
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") Long id) {
        return Response
                .status(Response.Status.OK)
                .entity(orderService.cancel(id).orElseThrow(() -> notFound(id)))
                .build();
    }

    @PUT
    @Path("/{id}/ship")
    public Response shipOrder(@PathParam("id") Long id) {
        return Response
                .status(Response.Status.OK)
                .entity(orderService.ship(id).orElseThrow(() -> notFound(id)))
                .build();
    }

    @PUT
    @Path("/{id}/deliver")
    public Response deliverOrder(@PathParam("id") Long id) {
        return Response
                .status(Response.Status.OK)
                .entity(orderService.deliver(id).orElseThrow(() -> notFound(id)))
                .build();
    }



    private NotFoundException notFound(Long id) {
        return new NotFoundException("Order " + id + " not found");
    }
}
