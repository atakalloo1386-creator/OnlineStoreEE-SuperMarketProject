package com.mftplus.mftonlinestoreee.organization.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.dto.BranchDto;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationDto;
import com.mftplus.mftonlinestoreee.organization.dto.OrganizationRequest;
import com.mftplus.mftonlinestoreee.organization.service.BranchService;
import com.mftplus.mftonlinestoreee.organization.service.OrganizationService;
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
@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationApi {
    @Inject
    private OrganizationService organizationService;
    @Inject
    private BranchService branchService;

    @GET
    public List<OrganizationDto> getOrganizations() {
        return organizationService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<OrganizationDto> getOrganizationsPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return organizationService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public OrganizationDto getOrganization(@PathParam("id") Long id) {
        return organizationService.findById(id).orElseThrow(() -> notFound(id));
    }

    @GET
    @Path("/{id}/branches")
    public List<BranchDto> getOrganizationBranches(@PathParam("id") Long id) {
        organizationService.findById(id).orElseThrow(() -> notFound(id));
        return branchService.findByOrganization(id);
    }

    @POST
    public Response createOrganization(@NotNull @Valid OrganizationRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(organizationService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public OrganizationDto updateOrganization(@PathParam("id") Long id, @NotNull @Valid OrganizationRequest request) {
        return organizationService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/activate")
    public OrganizationDto activateOrganization(@PathParam("id") Long id) {
        return organizationService.activate(id).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/deactivate")
    public OrganizationDto deactivateOrganization(@PathParam("id") Long id) {
        return organizationService.deactivate(id).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrganization(@PathParam("id") Long id) {
        organizationService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Organization " + id + " not found");
    }
}
