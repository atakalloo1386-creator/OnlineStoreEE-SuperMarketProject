package com.mftplus.mftonlinestoreee.organization.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentDto;
import com.mftplus.mftonlinestoreee.organization.dto.DepartmentRequest;
import com.mftplus.mftonlinestoreee.organization.dto.EmployeeDto;
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
@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentApi {
    @Inject
    private DepartmentService departmentService;

    @GET
    public List<DepartmentDto> getDepartments(@QueryParam("branchId") Long branchId,
                                              @QueryParam("userId") Long userId) {
        if (branchId != null) {
            return departmentService.findByBranch(branchId);
        }
        if (userId != null) {
            return departmentService.findByEmployee(userId);
        }
        return departmentService.findAll();
    }

    @GET
    @Path("/page")
    public PageResult<DepartmentDto> getDepartmentsPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                        @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return departmentService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public DepartmentDto getDepartment(@PathParam("id") Long id) {
        return departmentService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createDepartment(@NotNull @Valid DepartmentRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(departmentService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public DepartmentDto updateDepartment(@PathParam("id") Long id, @NotNull @Valid DepartmentRequest request) {
        return departmentService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/activate")
    public DepartmentDto activateDepartment(@PathParam("id") Long id) {
        return departmentService.activate(id).orElseThrow(() -> notFound(id));
    }

    @POST
    @Path("/{id}/deactivate")
    public DepartmentDto deactivateDepartment(@PathParam("id") Long id) {
        return departmentService.deactivate(id).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDepartment(@PathParam("id") Long id) {
        departmentService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/employees")
    public List<EmployeeDto> getEmployees(@PathParam("id") Long id) {
        return departmentService.findEmployees(id).orElseThrow(() -> notFound(id));
    }

    @PUT
    @Path("/{id}/employees/{userId}")
    public DepartmentDto addEmployee(@PathParam("id") Long id, @PathParam("userId") Long userId) {
        return departmentService.addEmployee(id, userId).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}/employees/{userId}")
    public DepartmentDto removeEmployee(@PathParam("id") Long id, @PathParam("userId") Long userId) {
        return departmentService.removeEmployee(id, userId).orElseThrow(() -> notFound(id));
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Department " + id + " not found");
    }
}
