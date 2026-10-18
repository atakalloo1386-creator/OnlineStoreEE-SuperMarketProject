package com.mftplus.mftonlinestoreee.products.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.products.dto.ProductDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductRequest;
import com.mftplus.mftonlinestoreee.products.service.ProductService;
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
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ProductApi {
    @Inject
    private ProductService productService;

    @GET
    public List<ProductDto> getProducts(
            @QueryParam("categoryId") Long categoryId
    ) {
        if (categoryId != null) {
            return productService.findByCategory(categoryId);
        }

        return productService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<ProductDto> getProductsPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                  @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return productService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public ProductDto getProduct(@PathParam("id") long id) {
        return productService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createProduct(@NotNull @Valid ProductRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(productService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public ProductDto updateProduct(@PathParam("id") long id, @NotNull @Valid ProductRequest request) {
        return productService.update(id,request).orElseThrow(() -> notFound(id));
    }
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") long id) {
        productService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("product " + id + " not found");
    }
}
