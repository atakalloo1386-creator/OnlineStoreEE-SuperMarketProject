package com.mftplus.mftonlinestoreee.tickets.service;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;

import java.util.List;
import java.util.Optional;

public interface TicketMessageService {

    List<TicketMessageDto> findByTicket(Long ticketId);

    Optional<TicketMessageDto> addMessage(Long ticketId, TicketMessageRequest request);
}
