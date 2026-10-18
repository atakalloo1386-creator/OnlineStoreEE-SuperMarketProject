package com.mftplus.mftonlinestoreee.users.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.users.dto.*;
import com.mftplus.mftonlinestoreee.users.service.UserService;
import jakarta.annotation.security.RolesAllowed;
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
@Path("/users")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserApi {
    @Inject
    private UserService userService;

    @GET
    public List<UserDto> getUsers() {
        return userService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<UserDto> getUsersPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return userService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public UserDto getUser(@PathParam("id") Long id) {
        return userService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createUser(@NotNull @Valid UserRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(userService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public UserDto updateUser(@PathParam("id") Long id, @NotNull @Valid UserRequest request) {
        return userService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/profile")
    public ProfileDto getProfile(@PathParam("id") Long id) {
        return userService.findProfile(id).orElseThrow(() -> notFound(id));
    }

    @PUT
    @Path("/{id}/profile")
    public ProfileDto saveProfile(@PathParam("id") Long id, @NotNull @Valid ProfileRequest request) {
        return userService.saveProfile(id, request).orElseThrow(() -> notFound(id));
    }



    private NotFoundException notFound(Long id) {
        return new NotFoundException("User " + id + " not found");
    }
}
