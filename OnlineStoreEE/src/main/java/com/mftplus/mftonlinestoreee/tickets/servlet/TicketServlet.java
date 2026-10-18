package com.mftplus.mftonlinestoreee.tickets.servlet;

import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/tickets.do")
public class TicketServlet extends HttpServlet {

    @Inject
    private TicketService ticketService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");

        if (userId != null) {
            ticketService.findByUser(Long.valueOf(userId));
        } else {
            ticketService.findAll();
        }
    }
}
