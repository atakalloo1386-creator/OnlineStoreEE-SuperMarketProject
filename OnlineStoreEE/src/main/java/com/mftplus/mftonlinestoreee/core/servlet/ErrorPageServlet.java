package com.mftplus.mftonlinestoreee.core.servlet;

import com.mftplus.mftonlinestoreee.core.rest.ApiResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@WebServlet("/error")
public class ErrorPageServlet extends HttpServlet {

    private Jsonb jsonb;

    @Override
    public void init() {
        jsonb = JsonbBuilder.create();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Response.Status status = Response.Status.fromStatusCode(code == null ? 404 : code);
        if (status == null) {
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        String message = status.getFamily() == Response.Status.Family.SERVER_ERROR
                ? "Unexpected server error"
                : (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        response.setStatus(status.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setCharacterEncoding("UTF-8");
        jsonb.toJson(ApiResponse.error(status, message, null), response.getWriter());
    }

    @Override
    public void destroy() {
        try {
            jsonb.close();
        } catch (Exception ignored) {
        }
    }
}
