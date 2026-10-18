package com.mftplus.mftonlinestoreee.attachments.rest;

import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentContent;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentDto;
import com.mftplus.mftonlinestoreee.attachments.dto.AttachmentRequest;
import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.attachments.service.AttachmentService;
import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequestScoped
@Path("/attachments")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttachmentApi {
    @Inject
    private AttachmentService attachmentService;

    @GET
    @PermitAll
    public List<AttachmentDto> getAttachments(@QueryParam("ownerType") AttachmentOwnerType ownerType,
                                              @QueryParam("ownerId") Long ownerId) {
        if (ownerType == null || ownerId == null) {
            return attachmentService.findAll();
        }
        return attachmentService.findByOwner(ownerType, ownerId);
    }

    @GET
    @Path("/page")
    @PermitAll
    public PageResult<AttachmentDto> getAttachmentsPage(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                                        @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        return attachmentService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public AttachmentDto getAttachment(@PathParam("id") Long id) {
        return attachmentService.findById(id).orElseThrow(() -> notFound(id));
    }

    @POST
    public Response createAttachment(@NotNull @Valid AttachmentRequest request) {
        return Response
                .status(Response.Status.CREATED)
                .entity(attachmentService.create(request))
                .build();
    }

    @PUT
    @Path("/{id}")
    public AttachmentDto updateAttachment(@PathParam("id") Long id, @NotNull @Valid AttachmentRequest request) {
        return attachmentService.update(id, request).orElseThrow(() -> notFound(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAttachment(@PathParam("id") Long id) {
        attachmentService.deleteById(id).orElseThrow(() -> notFound(id));
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/content")
    @Consumes(MediaType.WILDCARD)
    @RolesAllowed("attachment:file")
    public AttachmentDto uploadContent(@PathParam("id") Long id,
                                       @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
                                       InputStream content) {
        String type = contentType == null ? MediaType.APPLICATION_OCTET_STREAM : contentType;
        return attachmentService.uploadContent(id, content, type).orElseThrow(() -> notFound(id));
    }

    @GET
    @Path("/{id}/content")
    @Produces(MediaType.WILDCARD)
    @RolesAllowed("attachment:file")
    public Response downloadContent(@PathParam("id") Long id) {
        AttachmentContent content = attachmentService.findContent(id)
                .orElseThrow(() -> new NotFoundException("Attachment " + id + " has no file"));
        String fileName = URLEncoder.encode(content.fileName(), StandardCharsets.UTF_8).replace("+", "%20");
        return Response.ok(content.data(), content.contentType())
                .header(HttpHeaders.CONTENT_LENGTH, content.fileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .build();
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Attachment " + id + " not found");
    }
}
