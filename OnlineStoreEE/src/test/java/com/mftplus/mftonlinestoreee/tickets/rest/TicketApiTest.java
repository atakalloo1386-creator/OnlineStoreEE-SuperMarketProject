package com.mftplus.mftonlinestoreee.tickets.rest;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.tickets.service.TicketService;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.OPEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TicketApiTest {

    @Mock
    private TicketService service;
    @InjectMocks
    private TicketApi api;

    private AutoCloseable mocks;

    private final User user = TestDataFactory.createUser();

    private final TicketRequest request = new TicketRequest(
            LocalDateTime.of(2025, 3, 15, 14, 30),
            LocalDateTime.of(2026, 5, 15, 14, 30),
            "Financial Issue",
            "Asqar",
            1L,
            OPEN
    );

    private final TicketDto ticket = new TicketDto(
            1L,
            LocalDateTime.of(2025, 3, 15, 14, 30),
            LocalDateTime.of(2026, 5, 15, 14, 30),
            "Financial Issue",
            "Asqar",
            1L,
            OPEN
    );

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
    void getTicketReturnsTheDto() {
        when(service.findById(1L)).thenReturn(Optional.of(ticket));

        assertEquals(ticket, api.getTicket(1L));
    }

    @Test
    void getMissingTicketThrowsNotFound() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.getTicket(99L));
    }

    @Test
    void getTicketsByUser() {
        when(service.findByUser(1L)).thenReturn(List.of(ticket));

        assertEquals(List.of(ticket), api.getTickets(1L));
    }

    @Test
    void getAllTickets() {
        when(service.findAll()).thenReturn(List.of(ticket));

        assertEquals(List.of(ticket), api.getTickets(null));
    }

    @Test
    void createAnswers201WithTheCreatedDto() {
        when(service.create(request)).thenReturn(ticket);

        Response response = api.createTicket(request);

        assertEquals(201, response.getStatus());
        assertEquals(ticket, response.getEntity());
    }

    @Test
    void updateOfMissingTicketThrowsNotFound() {
        when(service.update(99L, request)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.updateTicket(99L, request));
    }

    @Test
    void deleteAnswers204() {
        when(service.deleteById(1L)).thenReturn(Optional.of(ticket));

        assertEquals(204, api.deleteTicket(1L).getStatus());
    }

    @Test
    void closeTicketReturnsTheDto() {
        TicketDto closed = new TicketDto(
                1L,
                LocalDateTime.of(2025, 3, 15, 14, 30),
                LocalDateTime.of(2026, 5, 15, 14, 30),
                "Financial Issue",
                "Asqar",
                1L,
                TicketStatus.CLOSED
        );
        when(service.close(1L)).thenReturn(Optional.of(closed));

        assertEquals(closed, api.closeTicket(1L));
    }

    @Test
    void closeMissingTicketThrowsNotFound() {
        when(service.close(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.closeTicket(99L));
    }

    @Test
    void getTicketsPage() {
        PageResult<TicketDto> page = PageResult.of(List.of(ticket), 0, 10, 1);
        when(service.findAll(0, 10)).thenReturn(page);

        assertEquals(page, api.getTicketsPage(0, 10));
    }
}