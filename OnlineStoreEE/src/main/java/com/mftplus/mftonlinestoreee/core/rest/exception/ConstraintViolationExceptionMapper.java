package com.mftplus.mftonlinestoreee.core.rest.exception;

import com.mftplus.mftonlinestoreee.core.rest.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return ApiResponse.errorResponse(Response.Status.BAD_REQUEST, "Validation failed", details(exception));
    }

    private List<String> details(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(violation -> fieldName(violation) + ": " + violation.getMessage())
                .sorted()
                .toList();
    }

    private String fieldName(ConstraintViolation<?> violation) {
        String name = null;
        for (Path.Node node : violation.getPropertyPath()) {
            name = node.getName();
        }
        return name;
    }
}
