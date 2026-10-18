package com.mftplus.mftonlinestoreee.core.rest.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;
import com.mftplus.mftonlinestoreee.core.rest.ApiResponse;
import jakarta.json.JsonException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof BusinessException) {
            return ApiResponse.errorResponse(Response.Status.CONFLICT, exception.getMessage(), null);
        }
        if (exception instanceof JsonException) {
            return ApiResponse.errorResponse(Response.Status.BAD_REQUEST, "Malformed JSON: " + exception.getMessage(), null);
        }
        if (exception instanceof WebApplicationException webException) {
            Response response = webException.getResponse();
            if (response.hasEntity()) {
                return response;
            }
            return Response.fromResponse(response)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(response.getStatusInfo(), exception.getMessage(), null))
                    .build();
        }
        log.error("Unhandled exception", exception);
        return ApiResponse.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected server error", null);
    }
}
