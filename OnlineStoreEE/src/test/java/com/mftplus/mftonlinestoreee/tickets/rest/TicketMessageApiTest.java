package com.mftplus.mftonlinestoreee.tickets.rest;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.tickets.service.TicketMessageService;
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

class TicketMessageApiTest {

    @Mock
    private TicketMessageService ticketMessageService;
    @Mock
    private TicketService ticketService;
    @InjectMocks
    private TicketMessageApi api;

    private AutoCloseable mocks;

    private final User user = TestDataFactory.createUser();

    private final TicketMessageRequest request = new TicketMessageRequest(
            LocalDateTime.of(2025, 3, 15, 14, 30),
            10L,
            1L,
            "This is a test message"
    );

    private final TicketMessageDto message = new TicketMessageDto(
            1L,
            LocalDateTime.of(2025, 3, 15, 14, 30),
            10L,
            1L,
            "Asqar",
            "This is a test message"
    );

    private final TicketDto ticket = new TicketDto(
            10L,
            LocalDateTime.of(2025, 3, 15, 14, 30),
            LocalDateTime.of(2026, 5, 15, 14, 30),
            "Test Ticket",
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
    void getMessagesReturnsTheList() {
        when(ticketService.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketMessageService.findByTicket(10L)).thenReturn(List.of(message));

        assertEquals(List.of(message), api.getMessages(10L));
    }

    @Test
    void getMessagesOfMissingTicketThrowsNotFound() {
        when(ticketService.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.getMessages(99L));
    }

    @Test
    void createMessageAnswers201WithTheCreatedDto() {
        when(ticketMessageService.addMessage(10L, request)).thenReturn(Optional.of(message));

        Response response = api.createMessage(10L, request);

        assertEquals(201, response.getStatus());
        assertEquals(message, response.getEntity());
    }

    @Test
    void createMessageOfMissingTicketThrowsNotFound() {
        when(ticketMessageService.addMessage(99L, request)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> api.createMessage(99L, request));
    }
}