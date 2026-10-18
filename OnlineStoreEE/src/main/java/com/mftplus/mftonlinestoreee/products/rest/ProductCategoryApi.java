package com.mftplus.mftonlinestoreee.products.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryDto;
import com.mftplus.mftonlinestoreee.products.dto.ProductCategoryRequest;
import com.mftplus.mftonlinestoreee.products.service.ProductCategoryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path("/products/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)


public class ProductCategoryApi {
    @Inject
    private ProductCategoryService productCategoryService;

    @GET
    public List<ProductCategoryDto> getProductCategories() {
        return productCategoryService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<ProductCategoryDto> getProductCategoriesPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                  @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return productCategoryService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public ProductCategoryDto getProductCategory(@PathParam("id") long id) {
        return productCategoryService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createProductCategory(@NotNull @Valid ProductCategoryRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(productCategoryService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public ProductCategoryDto updateProductCategory(@PathParam("id") long id, @NotNull @Valid ProductCategoryRequest request) {
        return productCategoryService.update(id,request).orElseThrow(() -> notFound(id));
    }
    @DELETE
    @Path("/{id}")
    public Response deleteProductCategory(@PathParam("id") long id) {
        productCategoryService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("product category " + id + " not found");
    }

    @GET
    @Path("/{id}/sub-categories")
    public List<ProductCategoryDto> getSubCategories(
            @PathParam("id") long id
    ) {
        return productCategoryService.findSubCategories(id);
    }
}
