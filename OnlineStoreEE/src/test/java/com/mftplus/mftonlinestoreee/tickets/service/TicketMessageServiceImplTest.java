package com.mftplus.mftonlinestoreee.tickets.service;

import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageDto;
import com.mftplus.mftonlinestoreee.tickets.dto.TicketMessageRequest;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.tickets.exception.TicketClosedException;
import com.mftplus.mftonlinestoreee.tickets.exception.UserNotFoundException;
import com.mftplus.mftonlinestoreee.tickets.mapper.TicketMessageMapper;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketMessageRepository;
import com.mftplus.mftonlinestoreee.tickets.repository.TicketRepository;
import com.mftplus.mftonlinestoreee.tickets.service.impl.TicketMessageServiceImpl;
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

class TicketMessageServiceImplTest {

    @Mock
    private TicketMessageRepository repository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private TicketMessageMapper mapper = Mappers.getMapper(TicketMessageMapper.class);
    @InjectMocks
    private TicketMessageServiceImpl service;

    private AutoCloseable mocks;

    private final User owner = TestDataFactory.createUser();
    private final User support = TestDataFactory.createUser();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        owner.setId(1L);
        support.setId(2L);
        support.setUsername("support");
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void addMessageFromOwnerKeepsStatusOpen() {
        Ticket ticket = ticket(10L, OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(repository.save(any(TicketMessage.class))).thenAnswer(call -> {
            TicketMessage msg = call.getArgument(0);
            msg.setId(1L);
            return msg;
        });

        Optional<TicketMessageDto> dto = service.addMessage(10L, request(1L, "Hello from owner"));

        assertTrue(dto.isPresent());
        assertEquals("Hello from owner", dto.get().content());
        assertEquals(OPEN, ticket.getStatus());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void addMessageFromSupportSetsStatusInProgress() {
        Ticket ticket = ticket(10L, OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(support));
        when(repository.save(any(TicketMessage.class))).thenAnswer(call -> {
            TicketMessage msg = call.getArgument(0);
            msg.setId(1L);
            return msg;
        });

        Optional<TicketMessageDto> dto = service.addMessage(10L, request(2L, "Hello from support"));

        assertTrue(dto.isPresent());
        assertEquals(IN_PROGRESS, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void addMessageToClosedTicketFails() {
        Ticket ticket = ticket(10L, CLOSED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketClosedException.class,
                () -> service.addMessage(10L, request(1L, "Should fail")));
        verify(repository, never()).save(any());
    }

    @Test
    void addMessageWithUnknownSenderFails() {
        Ticket ticket = ticket(10L, OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.addMessage(10L, request(99L, "Unknown sender")));
        verify(repository, never()).save(any());
    }

    @Test
    void addMessageToMissingTicketReturnsEmpty() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.addMessage(99L, request(1L, "x")).isEmpty());
    }

    @Test
    void findByTicketReturnsMappedList() {
        when(repository.findByTicket(10L)).thenReturn(List.of(
                TicketMessage.builder()
                        .id(1L)
                        .content("msg1")
                        .ticket(ticket(10L, OPEN))
                        .sender(owner)
                        .build()
        ));

        List<TicketMessageDto> result = service.findByTicket(10L);

        assertEquals(1, result.size());
        assertEquals("msg1", result.get(0).content());
    }

    private TicketMessageRequest request(Long senderId, String content) {
        return new TicketMessageRequest(
                LocalDateTime.of(2025, 3, 15, 14, 30),
                10L,
                senderId,
                content
        );
    }

    private Ticket ticket(Long id, TicketStatus status) {
        return Ticket.builder()
                .id(id)
                .subject("Test Ticket")
                .user(owner)
                .status(status)
                .build();
    }
}