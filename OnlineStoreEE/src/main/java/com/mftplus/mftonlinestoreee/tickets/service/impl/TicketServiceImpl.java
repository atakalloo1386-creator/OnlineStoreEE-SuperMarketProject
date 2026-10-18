package com.mftplus.mftonlinestoreee.tickets.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.exception.TicketClosedException;
import com.mftplus.mftonlinestoreee.tickets.exception.UserNotFoundException;
import com.mftplus.mftonlinestoreee.tickets.mapper.TicketMapper;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketRepository;
import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import com.mftplus.mftonlinestoreee.users.entity.User;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TicketServiceImpl implements TicketService {

    @Inject
    private TicketRepository repository;

    @Inject
    private TicketMapper mapper;

    @Inject
    private UserRepository userRepository;

    @Transactional
    @Override
    public TicketDto create(TicketRequest request) {
        Ticket ticket = mapper.toEntity(request);
        ticket.setUser(loadUser(request.userId()));
        ticket.setStatus(TicketStatus.OPEN);
        return mapper.toDto(repository.save(ticket));
    }

    @Override
    public Optional<TicketDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<TicketDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<TicketDto> findAll(int page, int size) {
        List<Ticket> tickets = repository.findAll(page, size);
        List<TicketDto> dtos = mapper.toDtoList(tickets);
        long total = repository.count();

        return PageResult.of(dtos, page, size, total);
    }

    public PageResult<TicketDto> findPage(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<TicketDto> update(Long id, TicketRequest request) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isEmpty()) {
            return Optional.empty();
        }

        Ticket ticket = ticketOpt.get();
        checkNotClosed(ticket);

        ticket.setSubject(request.subject());
        repository.save(ticket);
        return Optional.of(mapper.toDto(ticket));
    }

    @Transactional
    @Override
    public Optional<TicketDto> deleteById(Long id) {
        return repository.findById(id)
                .map(ticket -> {
                    repository.delete(ticket);
                    return mapper.toDto(ticket);
                });
    }

    @Override
    public List<TicketDto> findByUser(Long userId) {
        return mapper.toDtoList(repository.findByUser(userId));
    }

    @Transactional
    @Override
    public Optional<TicketDto> close(Long id) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isEmpty()) {
            return Optional.empty();
        }

        Ticket ticket = ticketOpt.get();
        checkNotClosed(ticket);

        ticket.setStatus(TicketStatus.CLOSED);
        repository.save(ticket);
        return Optional.of(mapper.toDto(ticket));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId, "User"));
    }

    private void checkNotClosed(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosedException(ticket.getId());
        }

    }
}
