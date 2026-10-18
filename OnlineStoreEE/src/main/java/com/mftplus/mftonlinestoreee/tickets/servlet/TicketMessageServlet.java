package com.mftplus.mftonlinestoreee.tickets.servlet;

import com.mftplus.mftonlinestoreee.tickets.service.TicketMessageService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ticket-messages.do")
public class TicketMessageServlet extends HttpServlet {

    @Inject
    private TicketMessageService ticketMessageService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ticketId = req.getParameter("ticketId");

        if (ticketId != null) {
            ticketMessageService.findByTicket(Long.valueOf(ticketId));
        }
    }
}
