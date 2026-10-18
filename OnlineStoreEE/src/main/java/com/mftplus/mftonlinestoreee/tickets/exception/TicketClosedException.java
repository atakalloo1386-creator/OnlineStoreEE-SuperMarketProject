package com.mftplus.mftonlinestoreee.tickets.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class TicketClosedException extends BusinessException {
    public TicketClosedException(Long ticketId) {
        super("Ticket with id " + ticketId + " is closed");
    }
}
