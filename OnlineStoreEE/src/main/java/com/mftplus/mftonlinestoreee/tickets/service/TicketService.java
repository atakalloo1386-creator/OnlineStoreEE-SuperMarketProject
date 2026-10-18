package com.mftplus.mftonlinestoreee.tickets.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;

import java.util.List;
import java.util.Optional;

public interface TicketService extends BaseService<TicketRequest, TicketDto>{

    List<TicketDto> findByUser(Long userId);

    Optional<TicketDto> close(Long id);
}
