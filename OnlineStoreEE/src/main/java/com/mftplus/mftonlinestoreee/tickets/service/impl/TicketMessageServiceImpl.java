package com.mftplus.mftonlinestoreee.tickets.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.exception.TicketClosedException;
import com.mftplus.mftonlinestoreee.tickets.exception.UserNotFoundException;
import com.mftplus.mftonlinestoreee.tickets.mapper.TicketMessageMapper;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketMessageRepository;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketRepository;
import com.mftplus.mftonlinestoreee.tickets.service.TicketMessageService;
import com.mftplus.mftonlinestoreee.users.entity.User;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class TicketMessageServiceImpl implements TicketMessageService {

    @Inject
    private TicketMessageRepository repository;

    @Inject
    private TicketMessageMapper mapper;

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private UserRepository userRepository;

    @Transactional
    public TicketMessageDto create(TicketMessageRequest request) {
        TicketMessage ticketMessage = mapper.toEntity(request);
        return mapper.toDto(repository.save(ticketMessage));
    }

    public Optional<TicketMessageDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public List<TicketMessageDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public PageResult<TicketMessageDto> findPage(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Override
    @Transactional
    public List<TicketMessageDto> findByTicket(Long ticketId) {
        return mapper.toDtoList(repository.findByTicket(ticketId));
    }

    @Transactional
    @Override
    public Optional<TicketMessageDto> addMessage(Long ticketId, TicketMessageRequest request) {
        return ticketRepository.findById(ticketId)
                .map(ticket -> {
                    checkNotClosed(ticket);

                    User sender = loadUser(request.senderId());

                    if (!Objects.equals(sender.getId(), ticket.getUser().getId())) {
                        ticket.setStatus(TicketStatus.IN_PROGRESS);
                        ticketRepository.save(ticket);
                    }

                    TicketMessage message = mapper.toEntity(request);
                    message.setTicket(ticket);
                    message.setSender(sender);

                    return mapper.toDto(repository.save(message));
                });
    }

    private User loadUser(Long senderId) {
        return userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException(senderId, "Sender"));
    }

    private void checkNotClosed(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosedException(ticket.getId());
        }
    }
}
