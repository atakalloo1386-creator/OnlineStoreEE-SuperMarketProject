package com.mftplus.mftonlinestoreee.organization.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.dto.BranchDto;
import com.mftplus.mftonlinestoreee.organization.dto.BranchRequest;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.service.BranchService;
import com.mftplus.mftonlinestoreee.organization.service.DepartmentService;
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
@Path("/branches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BranchApi {
    @Inject
    private BranchService branchService;
    @Inject
    private DepartmentService departmentService;

    @GET
    public List<BranchDto> getBranches(@QueryParam("organizationId") Long organizationId) {
        if (organizationId == null) {
            return branchService.findAll();
        }
        return branchService.findByOrganization(organizationId);
    }

    @GET
    @Path("/page")
    public PageResult<BranchDto> getBranchesPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                 @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return branchService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public BranchDto getBranch(@PathParam("id") Long id) {
        return branchService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createBranch(@NotNull @Valid BranchRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(branchService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public BranchDto updateBranch(@PathParam("id") Long id, @NotNull @Valid BranchRequest request) {
        return branchService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/activate")
    public BranchDto activateBranch(@PathParam("id") Long id) {
        return branchService.activate(id).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/deactivate")
    public BranchDto deactivateBranch(@PathParam("id") Long id) {
        return branchService.deactivate(id).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBranch(@PathParam("id") Long id) {
        branchService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/departments")
    public List<DepartmentDto> getBranchDepartments(@PathParam("id") Long id) {
        branchService.findById(id).orElseThrow(() -> notFound(id));
        return departmentService.findByBranch(id);
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Branch " + id + " not found");
    }
}
