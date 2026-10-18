package com.mftplus.mftonlinestoreee.core.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;

public record ApiResponse(
        boolean success,
        int status,
        String message,
        Object data,
        List<String> errors,
        String timestamp) {

    public static ApiResponse ok(Response.StatusType status, Object data) {
        return new ApiResponse(true, status.getStatusCode(), null, data, null, Instant.now().toString());
    }

    public static ApiResponse error(Response.StatusType status, String message, List<String> errors) {
        return new ApiResponse(false, status.getStatusCode(), message, null, errors, Instant.now().toString());
    }

    public static Response errorResponse(Response.StatusType status, String message, List<String> errors) {
        return Response.status(status.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(error(status, message, errors))
                .build();
    }
}
