package com.mftplus.mftonlinestoreee.core.rest.filter;

import com.mftplus.mftonlinestoreee.core.rest.ApiResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import java.io.InputStream;

@Provider
public class ApiResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (!shouldWrap(response)) {
            return;
        }
        response.setEntity(
                ApiResponse.ok(response.getStatusInfo(), response.getEntity()),
                response.getEntityAnnotations(),
                MediaType.APPLICATION_JSON_TYPE);
    }

    private boolean shouldWrap(ContainerResponseContext response) {
        Object entity = response.getEntity();
        MediaType mediaType = response.getMediaType();
        return entity != null
                && !(entity instanceof ApiResponse)
                && !(entity instanceof InputStream)
                && response.getStatus() < 400
                && mediaType != null
                && MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }
}
