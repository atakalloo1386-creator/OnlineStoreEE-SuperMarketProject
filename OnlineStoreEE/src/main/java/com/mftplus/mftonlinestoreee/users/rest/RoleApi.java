package com.mftplus.mftonlinestoreee.users.rest;

import com.mftplus.mftonlinestoreee.users.dto.RoleDto;
import com.mftplus.mftonlinestoreee.users.dto.RoleRequest;
import com.mftplus.mftonlinestoreee.users.service.RoleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@RequestScoped
@Path("/roles")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleApi {

    @Inject
    private RoleService roleService;

    @GET
    public List<RoleDto> getRoles(){
        return roleService.findAll();

    }

    @GET
    @Path("/{id}")
    public RoleDto getRole(@PathParam("id") Long id) {
        return roleService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createRole(@NotNull @Valid RoleRequest request){
        return Response
                .status(Response.Status.CREATED)
                .entity(roleService.create(request))
                .build();

    }

    @PUT
    @Path("/{id}")
    public RoleDto updateRole(@PathParam("id") Long id, @NotNull @Valid RoleRequest request) {
        return roleService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") Long id) {
        roleService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }


    private NotFoundException notFound(Long id) {
        return new NotFoundException("Role " + id + " not found");
    }
}
