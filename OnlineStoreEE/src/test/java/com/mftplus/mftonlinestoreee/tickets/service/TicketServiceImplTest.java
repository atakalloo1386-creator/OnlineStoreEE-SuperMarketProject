package com.mftplus.mftonlinestoreee.tickets.service;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.tickets.exception.TicketClosedException;
import com.mftplus.mftonlinestoreee.tickets.exception.UserNotFoundException;
import com.mftplus.mftonlinestoreee.tickets.mapper.TicketMapper;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketRepository;
import com.mftplus.mftonlinestoreee.tickets.service.impl.TicketServiceImpl;
import com.mftplus.mftonlinestoreee.users.entity.User;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository repository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private TicketMapper mapper = Mappers.getMapper(TicketMapper.class);
    @InjectMocks
    private TicketServiceImpl service;

    private AutoCloseable mocks;

    private final User user = TestDataFactory.createUser();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        user.setId(1L);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createSavesAndReturnsTheDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(Ticket.class))).thenAnswer(call -> {
            Ticket ticket = call.getArgument(0);
            ticket.setId(1L);
            return ticket;
        });

        TicketDto dto = service.create(request("Financial Issue"));

        assertEquals(1L, dto.id());
        assertEquals("Financial Issue", dto.subject());
        assertEquals(OPEN, dto.ticketStatus());
        assertEquals(user.getUsername(), dto.username());
    }

    @Test
    void createWithUnknownUserFails() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.create(requestWithUserId(99L)));
        verify(repository, never()).save(any());
    }

    @Test
    void updateChangesSubject() {
        Ticket existing = ticket(1L, "Old Subject", OPEN);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Ticket.class))).thenAnswer(call -> call.getArgument(0));

        Optional<TicketDto> dto = service.update(1L, request("Updated Subject"));

        assertEquals("Updated Subject", dto.orElseThrow().subject());
        assertEquals(OPEN, existing.getStatus());
    }

    @Test
    void updateOfClosedTicketFails() {
        Ticket existing = ticket(1L, "Closed Ticket", CLOSED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(TicketClosedException.class, () -> service.update(1L, request("Try Update")));
        verify(repository, never()).save(any());
    }

    @Test
    void updateOfMissingTicketReturnsEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.update(99L, request("x")).isEmpty());
    }

    @Test
    void closeSetsStatusToClosed() {
        Ticket existing = ticket(1L, "Open Ticket", OPEN);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Ticket.class))).thenAnswer(call -> call.getArgument(0));

        Optional<TicketDto> dto = service.close(1L);

        assertEquals(CLOSED, dto.orElseThrow().ticketStatus());
        assertEquals(CLOSED, existing.getStatus());
    }

    @Test
    void closeOfAlreadyClosedTicketFails() {
        Ticket existing = ticket(1L, "Already Closed", CLOSED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(TicketClosedException.class, () -> service.close(1L));
    }

    @Test
    void deleteRemovesTheTicket() {
        Ticket existing = ticket(1L, "To Delete", OPEN);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertTrue(service.deleteById(1L).isPresent());
        verify(repository).delete(existing);
    }

    @Test
    void findByUserReturnsMappedList() {
        when(repository.findByUser(1L)).thenReturn(List.of(ticket(1L, "Ticket 1", OPEN)));

        List<TicketDto> result = service.findByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Ticket 1", result.get(0).subject());
    }

    private TicketRequest request(String subject) {
        return new TicketRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                subject,
                user.getUsername(),
                1L,
                OPEN
        );
    }

    private TicketRequest requestWithUserId(Long userId) {
        return new TicketRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                "Subject",
                user.getUsername(),
                userId,
                OPEN
        );
    }

    private Ticket ticket(Long id, String subject, TicketStatus status) {
        return Ticket.builder()
                .id(id)
                .subject(subject)
                .user(user)
                .status(status)
                .build();
    }
}